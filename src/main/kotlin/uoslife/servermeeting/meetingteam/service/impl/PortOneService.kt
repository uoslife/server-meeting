package uoslife.servermeeting.meetingteam.service.impl

import jakarta.transaction.Transactional
import java.util.*
import javax.naming.NameNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import uoslife.servermeeting.global.auth.exception.ExternalApiFailedException
import uoslife.servermeeting.meetingteam.dao.MeetingTeamDao
import uoslife.servermeeting.meetingteam.dao.PaymentDao
import uoslife.servermeeting.meetingteam.dao.UserTeamDao
import uoslife.servermeeting.meetingteam.dto.request.PaymentRequestDto
import uoslife.servermeeting.meetingteam.dto.response.PaymentResponseDto
import uoslife.servermeeting.meetingteam.entity.Payment
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.exception.*
import uoslife.servermeeting.meetingteam.repository.PaymentRepository
import uoslife.servermeeting.meetingteam.repository.UserTeamRepository
import uoslife.servermeeting.meetingteam.service.PaymentService
import uoslife.servermeeting.meetingteam.util.Validator
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
    override fun createPayment(teamType: TeamType): Payment {
        val payment =
            Payment.createPayment(
                UUID.randomUUID().toString(),
                when (teamType) {
                    TeamType.SINGLE -> singlePrice
                    TeamType.TRIPLE -> triplePrice
                },
                PaymentStatus.NONE,
            )

        return paymentRepository.save(payment)
    }

    @Transactional
    override fun checkPayment(
        userId: Long,
        teamType: TeamType,
        paymentCheckRequest: PaymentRequestDto.PaymentCheckRequest
    ): PaymentResponseDto.PaymentCheckResponse {
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()

        val payment =
            paymentDao.getPaymentWithUserAndTeamType(user, teamType)
                ?: throw PaymentNotFoundException()

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
        teamType: TeamType
    ): PaymentResponseDto.PaymentRefundResponse {
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()

        val payment =
            paymentDao.getPaymentWithUserAndTeamType(user, teamType)
                ?: throw PaymentNotFoundException()

        if (!validator.isAlreadyPaid(payment)) throw PaymentInValidException()

        try {
            val accessToken = portOneAPIService.getAccessToken(impKey, impSecret)
            portOneAPIService.refundPayment(
                accessToken.response!!.access_token,
                payment.impUid,
                payment.price
            )

            payment.status = PaymentStatus.REFUND
            logger.info(
                "[환불 성공] payment_id: ${payment.id}, impUid: ${payment.impUid}, marchantUid: ${payment.merchantUid}"
            )
        } catch (e: ExternalApiFailedException) {
            payment.status = PaymentStatus.REFUND_FAILED
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
        val accessToken = portOneAPIService.getAccessToken(impKey, impSecret)
        paymentList.map { payment ->
            logger.info(
                "[환불 성공] payment_id: ${payment.id}, impUid: ${payment.impUid}, marchantUid: ${payment.merchantUid}"
            )
            try {
                portOneAPIService.refundPayment(
                    accessToken.response!!.access_token,
                    payment.impUid,
                    payment.price
                )
                payment.status = PaymentStatus.REFUND
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

    override fun verifyPayment(
        userId: Long,
        teamType: TeamType
    ): PaymentResponseDto.PaymentRequestResponse {
        // 미팅팀이 없으면, 신청하기 버튼
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        val phoneNumber = user.phoneNumber ?: throw PhoneNumberNotFoundException()

        val payment =
            paymentDao.getPaymentWithUserAndTeamType(user, teamType)
                ?: throw PaymentNotFoundException()

        // 결제가 이미 성공했다면, 신청 정보 확인하기 버튼
        if (validator.isAlreadyPaid(payment)) throw UserAlreadyHavePaymentException()

        // 결제가 저장되어 있지만 결제 승인 확인 안됐다면, 결제 검증
        return PaymentResponseDto.PaymentRequestResponse(
            payment.merchantUid!!,
            payment.price!!,
            phoneNumber,
            user.name ?: throw NameNotFoundException(),
            teamType
        )
    }

    @Transactional
    override fun deleteUserPayment(user: User) {
        //        paymentRepository.
    }

    @Transactional
    override fun synchronizePayment(
        paymentWebhookResponse: PaymentResponseDto.PaymentWebhookResponse
    ) {
        if (paymentWebhookResponse.isSuccess()) {
            val payment =
                paymentRepository.findByMerchantUid(paymentWebhookResponse.merchant_uid)
                    ?: throw PaymentNotFoundException()

            payment.updatePayment(paymentWebhookResponse.imp_uid, PaymentStatus.SUCCESS)
            logger.info("[결제 성공] marchantUid : ${payment.merchantUid}")
        }
        return
    }
}
