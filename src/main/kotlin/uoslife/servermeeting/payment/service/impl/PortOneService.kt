package uoslife.servermeeting.payment.service.impl

import jakarta.transaction.Transactional
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import uoslife.servermeeting.global.auth.exception.ExternalApiFailedException
import uoslife.servermeeting.meetingteam.dao.MeetingTeamDao
import uoslife.servermeeting.meetingteam.dao.UserTeamDao
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.UserTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.exception.*
import uoslife.servermeeting.meetingteam.repository.UserTeamRepository
import uoslife.servermeeting.meetingteam.util.Validator
import uoslife.servermeeting.payment.dao.PaymentDao
import uoslife.servermeeting.payment.dto.request.PaymentRequestDto
import uoslife.servermeeting.payment.dto.response.PaymentResponseDto
import uoslife.servermeeting.payment.entity.Payment
import uoslife.servermeeting.payment.entity.enums.PaymentStatus
import uoslife.servermeeting.payment.exception.PaymentInValidException
import uoslife.servermeeting.payment.exception.PaymentNotFoundException
import uoslife.servermeeting.payment.exception.RefundPrecedeException
import uoslife.servermeeting.payment.exception.UserAlreadyHavePaymentException
import uoslife.servermeeting.payment.repository.PaymentRepository
import uoslife.servermeeting.payment.service.PaymentService
import uoslife.servermeeting.user.dao.UserDao
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.exception.UserNotFoundException
import uoslife.servermeeting.user.repository.UserRepository

@Service
@Qualifier("PortOneService")
class PortOneService(
    private val userRepository: UserRepository,
    private val userDao: UserDao,
    private val userTeamDao: UserTeamDao,
    private val paymentDao: PaymentDao,
    private val meetingTeamDao: MeetingTeamDao,
    private val paymentRepository: PaymentRepository,
    private val validator: Validator,
    private val userTeamRepository: UserTeamRepository,
    private val portOneAPIService: PortOneAPIService,
    @Value("\${portone.api.price.single}") private val singlePrice: Int,
    @Value("\${portone.api.price.triple}") private val triplePrice: Int,
) : PaymentService {
    companion object {
        private val logger = LoggerFactory.getLogger(PaymentService::class.java)
    }

    // 결제 정보를 생성할 수 있는 유일한 로직
    @Transactional
    override fun requestPayment(
        userId: Long,
        paymentRequestPaymentRequest: PaymentRequestDto.PaymentRequestRequest,
        teamType: TeamType
    ): PaymentResponseDto.PaymentRequestResponse {
        val userTeam =
            userTeamDao.findUserWithMeetingTeam(userId, teamType)
                ?: throw UserTeamNotFoundException()
        val meetingTeam = userTeam.team
        val user = userTeam.user

        checkUserCanMakePayment(userTeam, userId, teamType)

        val payment = createNewPayment(user, meetingTeam)

        return PaymentResponseDto.PaymentRequestResponse(
            payment.merchantUid,
            payment.price,
            user.phoneNumber ?: throw PhoneNumberNotFoundException(),
            user.name ?: throw UserInfoNotCompletedException(),
            meetingTeam.type,
            payment.status
        )
    }

    private fun checkUserCanMakePayment(userTeam: UserTeam, userId: Long, teamType: TeamType) {
        checkUserIsLeader(userTeam)
        getSuccessPayment(userId, teamType)?.let { throw UserAlreadyHavePaymentException() }
        getPendingPayment(userId, teamType)?.let { throw UserAlreadyHavePaymentException() }
    }

    private fun createNewPayment(user: User, meetingTeam: MeetingTeam): Payment {
        val payment =
            Payment.createPayment(
                UUID.randomUUID().toString(),
                when (meetingTeam.type) {
                    TeamType.SINGLE -> singlePrice
                    TeamType.TRIPLE -> triplePrice
                },
                PaymentStatus.PENDING,
                meetingTeam,
                meetingTeam.type,
                user,
            )
        logger.info(
            "[결제 정보 생성] merchantId : ${payment.merchantUid}, teamType = ${payment.teamType}"
        )
        return paymentRepository.save(payment)
    }

    fun getPendingPayment(userId: Long, teamType: TeamType): Payment? {
        return paymentDao.getPendingPaymentFromUserIdAndTeamType(userId, teamType)
    }

    private fun checkUserIsLeader(userTeam: UserTeam) {
        if (!userTeam.isLeader) throw OnlyTeamLeaderCanCreatePaymentException()
    }

    override fun getSuccessPayment(userId: Long, teamType: TeamType): Payment? {
        return paymentDao.getSuccessPaymentFromUserIdAndTeamType(userId, teamType)
    }

    @Transactional
    override fun checkPayment(
        userId: Long,
        teamType: TeamType,
        paymentCheckRequest: PaymentRequestDto.PaymentCheckRequest
    ): PaymentResponseDto.PaymentCheckResponse {
        logger.info("[결제 검증 요청] UserId : $userId")
        paymentDao.getSuccessPaymentFromUserIdAndTeamType(userId, teamType)?.let {
            return PaymentResponseDto.PaymentCheckResponse(true, "")
        }

        val payment =
            paymentRepository.findByMerchantUid(paymentCheckRequest.merchantUid)
                ?: throw PaymentNotFoundException()

        try {
            val portOneStatus = portOneAPIService.checkPayment(paymentCheckRequest.impUid)

            if (portOneStatus.isPaid()) {
                updatePaymentStatus(payment, PaymentStatus.SUCCESS, paymentCheckRequest.impUid)
                return PaymentResponseDto.PaymentCheckResponse(true, "")
            }
        } catch (_: ExternalApiFailedException) {}

        updatePaymentStatus(payment, PaymentStatus.FAILED, paymentCheckRequest.impUid)
        return PaymentResponseDto.PaymentCheckResponse(false, "")
    }

    @Transactional
    override fun refundPaymentByToken(
        userId: Long,
        teamType: TeamType
    ): PaymentResponseDto.PaymentRefundResponse {
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()

        val payment =
            paymentDao.getSuccessPaymentFromUserIdAndTeamType(userId, teamType)
                ?: throw PaymentNotFoundException()

        if (!validator.isAlreadyPaid(payment)) throw PaymentInValidException()

        try {
            portOneAPIService.refundPayment(payment.impUid, payment.price)

            updatePaymentStatus(payment, PaymentStatus.REFUND, payment.impUid!!)
        } catch (e: ExternalApiFailedException) {
            return PaymentResponseDto.PaymentRefundResponse(false, "")
        }

        when (teamType) {
            TeamType.SINGLE -> {
                val userTeam = user.userTeams.first { it.team.type == TeamType.SINGLE }
                userTeamRepository.delete(userTeam)
            }
            TeamType.TRIPLE -> {
                val userTeam = user.userTeams.first { it.team.type == TeamType.TRIPLE }
                if (!userTeam.isLeader) throw OnlyTeamLeaderCanDeleteTeamException()
                val userTeams = userTeamDao.findByTeam(userTeam.team)
                userTeamRepository.deleteAll(userTeams)
            }
        }

        return PaymentResponseDto.PaymentRefundResponse(true, "")
    }

    @Transactional
    override fun refundPayment(): PaymentResponseDto.NotMatchedPaymentRefundResponse {
        val userList =
            meetingTeamDao
                .findUserIdWithMaleMeetingTeam()
                .plus(meetingTeamDao.findUserIdWithFeMaleMeetingTeam())
        val paymentList = userDao.findNotMatchedPayment(userList)
        logger.info("Total payment count: " + paymentList.size)

        val refundFailedList: MutableList<String> = mutableListOf()
        paymentList.forEach { payment ->
            try {
                portOneAPIService.refundPayment(payment.impUid, payment.price)
                updatePaymentStatus(payment, PaymentStatus.REFUND, payment.impUid!!)
            } catch (e: ExternalApiFailedException) {
                logger.info(
                    "[환불 실패] payment_id: ${payment.id}, impUid: ${payment.impUid}, marchantUid: ${payment.merchantUid}"
                )
                refundFailedList.add(payment.id.toString())
                payment.status = PaymentStatus.REFUND_FAILED
            }
        }
        return PaymentResponseDto.NotMatchedPaymentRefundResponse(refundFailedList)
    }

    @Transactional(dontRollbackOn = [PaymentNotFoundException::class])
    override fun verifyPayment(
        userId: Long,
        teamType: TeamType
    ): PaymentResponseDto.PaymentRequestResponse {
        // 미팅팀이 없으면, 신청하기 버튼
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        val phoneNumber = user.phoneNumber ?: throw PhoneNumberNotFoundException()

        val payments =
            paymentDao.getAllPaymentWithUserAndTeamType(user, teamType)
                ?: throw PaymentNotFoundException()

        var pendingPayment: Payment? = null

        payments.forEach { payment ->
            if (payment.isSuccess())
                return PaymentResponseDto.PaymentRequestResponse(
                    payment.merchantUid,
                    payment.price,
                    phoneNumber,
                    user.name ?: throw UserInfoNotCompletedException(),
                    payment.teamType,
                    payment.status
                )
            if (payment.status == PaymentStatus.PENDING) {
                pendingPayment = payment
            }
        }

        if (pendingPayment == null) throw PaymentNotFoundException()

        try {
            val portOneStatus = portOneAPIService.findPaymentByMID(pendingPayment!!.merchantUid)

            if (portOneStatus.isPaid()) {
                updatePaymentStatus(
                    pendingPayment!!,
                    PaymentStatus.SUCCESS,
                    portOneStatus.response?.imp_uid!!
                )
                return PaymentResponseDto.PaymentRequestResponse(
                    pendingPayment!!.merchantUid,
                    pendingPayment!!.price,
                    phoneNumber,
                    user.name ?: throw UserInfoNotCompletedException(),
                    pendingPayment!!.teamType,
                    pendingPayment!!.status
                )
            }
        } catch (_: ExternalApiFailedException) {}
        logger.info("[결제 검증 실패] merchantUid : ${pendingPayment!!.merchantUid}")
        pendingPayment!!.status = PaymentStatus.FAILED
        throw PaymentNotFoundException()
    }

    @Transactional
    override fun deleteUserPayment(user: User) {
        val userPayments = paymentRepository.findAllByUser(user) ?: return

        checkSuccessPayment(userPayments)

        userPayments.forEach { it.softDelete() }
    }

    private fun checkSuccessPayment(userPayments: List<Payment>) {
        userPayments.forEach { payment: Payment ->
            if (payment.isSuccess()) throw RefundPrecedeException()
        }
    }

    @Transactional
    override fun synchronizePayment(
        paymentWebhookResponse: PaymentResponseDto.PaymentWebhookResponse
    ) {
        logger.info("[웹훅 수신] status : ${paymentWebhookResponse.status}")
        if (paymentWebhookResponse.isSuccess()) {
            val payment =
                paymentRepository.findByMerchantUid(paymentWebhookResponse.merchant_uid!!)
                    ?: throw PaymentNotFoundException()

            updatePaymentStatus(payment, PaymentStatus.SUCCESS, paymentWebhookResponse.imp_uid!!)
        } else if (paymentWebhookResponse.isCancelled()) {
            val payment =
                paymentRepository.findByMerchantUid(paymentWebhookResponse.merchant_uid!!)
                    ?: throw PaymentNotFoundException()

            updatePaymentStatus(payment, PaymentStatus.FAILED, paymentWebhookResponse.imp_uid!!)
        }
        return
    }

    private fun updatePaymentStatus(payment: Payment, status: PaymentStatus, impUid: String) {
        payment.updatePayment(impUid, status)
        logger.info("[결제 상태 변경] paymentId: ${payment.id}, status: $status, impUid: $impUid")
    }
}
