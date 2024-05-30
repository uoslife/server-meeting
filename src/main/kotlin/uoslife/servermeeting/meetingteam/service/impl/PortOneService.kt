package uoslife.servermeeting.meetingteam.service.impl

import jakarta.transaction.Transactional
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import uoslife.servermeeting.global.auth.exception.ExternalApiFailedException
import uoslife.servermeeting.meetingteam.dao.MeetingTeamDao
import uoslife.servermeeting.meetingteam.dto.request.PaymentRequestDto
import uoslife.servermeeting.meetingteam.dto.response.PaymentResponseDto
import uoslife.servermeeting.meetingteam.entity.Payment
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.exception.*
import uoslife.servermeeting.meetingteam.repository.MeetingTeamRepository
import uoslife.servermeeting.meetingteam.repository.PaymentRepository
import uoslife.servermeeting.meetingteam.service.PaymentService
import uoslife.servermeeting.meetingteam.util.Validator
import uoslife.servermeeting.user.dao.UserDao
import uoslife.servermeeting.user.entity.NotMatchedUser
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.exception.UserNotFoundException
import uoslife.servermeeting.user.repository.UserRepository
import uoslife.servermeeting.user.service.NotMatchedUserService

@Service
@Qualifier("PortOneService")
class PortOneService(
    private val userRepository: UserRepository,
    private val userDao: UserDao,
    private val meetingTeamDao: MeetingTeamDao,
    private val paymentRepository: PaymentRepository,
    private val meetingTeamRepository: MeetingTeamRepository,
    private val validator: Validator,
    private val portOneAPIService: PortOneAPIService,
    private val notMatchedUserService: NotMatchedUserService,
    @Value("\${portone.api.url}") private val url: String,
    @Value("\${portone.api.price.single}") private val singlePrice: Int,
    @Value("\${portone.api.price.triple}") private val triplePrice: Int,
    @Value("\${portone.api.imp.key}") private val impKey: String,
    @Value("\${portone.api.imp.secret}") private val impSecret: String,
) : PaymentService {
    companion object {
        private val logger = LoggerFactory.getLogger(PaymentService::class.java)
    }
    @Transactional
    override fun requestPayment(
        userId: Long,
        paymentRequestPaymentRequest: PaymentRequestDto.PaymentRequestRequest
    ): PaymentResponseDto.PaymentRequestResponse {
        val user = userDao.findUserWithMeetingTeam(userId) ?: throw UserNotFoundException()
        val team = user.team ?: throw MeetingTeamNotFoundException()
        val phoneNumber = user.phoneNumber ?: throw PhoneNumberNotFoundException()

        if (paymentRepository.existsByUser(user)) {
            val payment = paymentRepository.findByUser(user)
            if (validator.isAlreadyPaid(payment!!)) throw UserAlreadyHavePaymentException()
            return PaymentResponseDto.PaymentRequestResponse(
                payment.marchantUid!!,
                payment.price!!,
                phoneNumber,
                user.name,
                team.type
            )
        }

        val payment =
            Payment.createPayment(
                user,
                paymentRequestPaymentRequest.pg,
                paymentRequestPaymentRequest.payMethod,
                UUID.randomUUID().toString(),
                when (team.type) {
                    TeamType.SINGLE -> singlePrice
                    TeamType.TRIPLE -> triplePrice
                },
                PaymentStatus.REQUEST,
            )

        paymentRepository.save(payment)

        return PaymentResponseDto.PaymentRequestResponse(
            payment.marchantUid!!,
            payment.price!!,
            phoneNumber,
            user.name,
            team.type
        )
    }

    @Transactional
    override fun checkPayment(
        userId: Long,
        paymentCheckRequest: PaymentRequestDto.PaymentCheckRequest
    ): PaymentResponseDto.PaymentCheckResponse {
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        val payment = paymentRepository.findByUser(user) ?: throw PaymentNotFoundException()

        try {
            val accessToken = portOneAPIService.getAccessToken(impKey, impSecret)
            portOneAPIService.checkPayment(
                accessToken.response!!.access_token,
                paymentCheckRequest.impUid
            )

            payment.impUid = paymentCheckRequest.impUid
            payment.status = PaymentStatus.SUCCESS
            return PaymentResponseDto.PaymentCheckResponse(true, "")
        } catch (e: ExternalApiFailedException) {
            payment.status = PaymentStatus.FAILED
            return PaymentResponseDto.PaymentCheckResponse(false, "")
        }
    }

    @Transactional
    override fun refundPaymentByToken(
        userId: Long,
    ): PaymentResponseDto.PaymentRefundResponse {
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        val payment = paymentRepository.findByUser(user) ?: throw PaymentNotFoundException()
        val team = user.team ?: throw MeetingTeamNotFoundException()

        if (!validator.isAlreadyPaid(payment)) throw PaymentInValidException()

        when (team.type) {
            TeamType.SINGLE -> user.team = null
            TeamType.TRIPLE -> team.users.forEach { it.team = null }
        }
        meetingTeamRepository.delete(team)

        try {
            val accessToken = portOneAPIService.getAccessToken(impKey, impSecret)
            portOneAPIService.refundPayment(
                accessToken.response!!.access_token,
                payment.impUid,
                payment.price
            )

            paymentRepository.delete(payment)
            return PaymentResponseDto.PaymentRefundResponse(true, "")
        } catch (e: ExternalApiFailedException) {
            payment.status = PaymentStatus.REFUND_FAILED
            return PaymentResponseDto.PaymentRefundResponse(false, "")
        }
    }

    @Transactional
    override fun refundPayment() {
        val notMatchedUserList: List<NotMatchedUser> = notMatchedUserService.findAll()
        val accessToken = portOneAPIService.getAccessToken(impKey, impSecret)

        notMatchedUserList.forEachIndexed{ index, user ->
            logger.info("[${index+1} 번째 환불] " + user)
            try {
                portOneAPIService.refundPayment(
                    accessToken.response!!.access_token,
                    user.impUid,
                    user.price
                )

                user.status = PaymentStatus.REFUND
            } catch (e: ExternalApiFailedException) {
                logger.info("[${index+1} 번째 환불 실패] " + user)
            }
        }
    }

    override fun verifyPayment(userId: Long): PaymentResponseDto.PaymentRequestResponse {
        // 미팅팀이 없으면, 신청하기 버튼
        val user = userDao.findUserWithMeetingTeam(userId) ?: throw UserNotFoundException()
        val team = user.team ?: throw MeetingTeamNotFoundException()
        val phoneNumber = user.phoneNumber ?: throw PhoneNumberNotFoundException()

        val payment =
            when (team.type) {
                TeamType.SINGLE -> paymentRepository.findByUser(user)
                TeamType.TRIPLE -> paymentRepository.findByUser(team.leader!!)
            }
                ?: throw PaymentNotFoundException()

        // 결제가 이미 성공했다면, 신청 정보 확인하기 버튼
        if (validator.isAlreadyPaid(payment)) throw UserAlreadyHavePaymentException()

        // 결제가 저장되어 있지만 결제 승인 확인 안됐다면, 결제 검증
        return PaymentResponseDto.PaymentRequestResponse(
            payment.marchantUid!!,
            payment.price!!,
            phoneNumber,
            user.name,
            team.type
        )
    }

    @Transactional
    override fun verifyPaymentError() {
        val paymentList = paymentRepository.findByStatus(PaymentStatus.REQUEST)
        val accessToken = portOneAPIService.getAccessToken(impKey, impSecret)
        for (payment in paymentList) {
            try {
                val result =
                    portOneAPIService.findPaymentByMID(
                        accessToken.response!!.access_token,
                        payment.marchantUid!!
                    )
                if (result.response!!.failed_at == 0) {
                    payment.impUid = result.response!!.imp_uid
                    payment.status = PaymentStatus.SUCCESS // 결제 성공
                }
            } catch (e: ExternalApiFailedException) {
                logger.info("MarchantUid-Name: " + payment.marchantUid + " " + payment.user!!.name)
            }
        }
    }

    /** 결제취소지만 DB에는 SUCCESS로 남아있는 데이터 REFUND로 변경 */
    @Transactional
    override fun verifyPaymentErrorRefundedButSuccess() {
        val paymentList = paymentRepository.findByStatus(PaymentStatus.SUCCESS)
        val accessToken = portOneAPIService.getAccessToken(impKey, impSecret)

        for (payment in paymentList) {
            try {
                val result =
                    portOneAPIService.findPaymentByMID(
                        accessToken.response!!.access_token,
                        payment.marchantUid!!
                    )
                if (result.response!!.cancelled_at != 0) { // DB에는 SUCCESS이지만 결제취소한 경우
                    logger.info(
                        "결제취소 MarchantUid-Name: " +
                            payment.marchantUid +
                            " " +
                            payment.user!!.name +
                            " " +
                            payment.status
                    )
                    payment.status = PaymentStatus.REFUND // 결제취소로 변경
                }
            } catch (e: ExternalApiFailedException) {
                logger.info(
                    "[Exception] MarchantUid-Name: " +
                        payment.marchantUid +
                        " " +
                        payment.user!!.name
                )
            }
        }
    }

    @Transactional
    override fun deleteUserPayment(user: User) {
        paymentRepository.deleteByUser(user)
    }
}
