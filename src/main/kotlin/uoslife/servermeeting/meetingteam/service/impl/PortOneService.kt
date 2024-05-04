package uoslife.servermeeting.meetingteam.service.impl

import jakarta.transaction.Transactional
import java.net.URI
import java.util.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import uoslife.servermeeting.global.auth.exception.ExternalApiFailedException
import uoslife.servermeeting.global.error.RestTemplateErrorHandler
import uoslife.servermeeting.meetingteam.dao.MeetingTeamDao
import uoslife.servermeeting.meetingteam.dto.request.PaymentRequestDto
import uoslife.servermeeting.meetingteam.dto.request.PortOneRequestDto
import uoslife.servermeeting.meetingteam.dto.response.PaymentResponseDto
import uoslife.servermeeting.meetingteam.dto.response.PortOneResponseDto
import uoslife.servermeeting.meetingteam.entity.Payment
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.exception.*
import uoslife.servermeeting.meetingteam.repository.MeetingTeamRepository
import uoslife.servermeeting.meetingteam.repository.PaymentRepository
import uoslife.servermeeting.meetingteam.service.PaymentService
import uoslife.servermeeting.meetingteam.util.Validator
import uoslife.servermeeting.user.dao.UserDao
import uoslife.servermeeting.user.exception.UserNotFoundException
import uoslife.servermeeting.user.repository.UserRepository

@Service
@Qualifier("PortOneService")
class PortOneService(
    private val userRepository: UserRepository,
    private val userDao: UserDao,
    private val meetingTeamDao: MeetingTeamDao,
    private val paymentRepository: PaymentRepository,
    private val meetingTeamRepository: MeetingTeamRepository,
    private val validator: Validator,
    private val restTemplateErrorHandler: RestTemplateErrorHandler,
    @Value("\${portone.api.url}") private val url: String,
    @Value("\${portone.api.price.single}") private val singlePrice: Int,
    @Value("\${portone.api.price.triple}") private val triplePrice: Int,
    @Value("\${portone.api.imp.key}") private val impKey: String,
    @Value("\${portone.api.imp.secret}") private val impSecret: String,
) : PaymentService {
    @Transactional
    override fun requestPayment(
        userUUID: UUID,
        paymentRequestPaymentRequest: PaymentRequestDto.PaymentRequestRequest
    ): PaymentResponseDto.PaymentRequestResponse {
        val user = userDao.findUserWithMeetingTeam(userUUID) ?: throw UserNotFoundException()
        val team = user.team ?: throw MeetingTeamNotFoundException()
        val phoneNumber = user.phoneNumber ?: throw PhoneNumberNotFoundException()

        paymentRepository.findByUser(user)?.let {
            if (validator.isAlreadyPaid(it)) throw UserAlreadyHavePaymentException()
            return PaymentResponseDto.PaymentRequestResponse(
                it.marchantUid!!,
                it.price!!,
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
        userUUID: UUID,
        paymentCheckRequest: PaymentRequestDto.PaymentCheckRequest
    ): PaymentResponseDto.PaymentCheckResponse {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()
        val payment = paymentRepository.findByUser(user) ?: throw PaymentNotFoundException()

        try {
            checkPaymentByPortOne(paymentCheckRequest.impUid)

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
        userUUID: UUID,
    ): PaymentResponseDto.PaymentRefundResponse {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()
        val payment = paymentRepository.findByUser(user) ?: throw PaymentNotFoundException()
        val team = user.team ?: throw MeetingTeamNotFoundException()

        if (!validator.isAlreadyPaid(payment)) throw PaymentInValidException()

        when (team.type) {
            TeamType.SINGLE -> user.team = null
            TeamType.TRIPLE -> team.users.forEach { it.team = null }
        }
        meetingTeamRepository.delete(team)

        try {
            refundPaymentByPortOne(payment)

            payment.status = PaymentStatus.REFUND
            return PaymentResponseDto.PaymentRefundResponse(true, "")
        } catch (e: ExternalApiFailedException) {
            payment.status = PaymentStatus.REFUND_FAILED
            return PaymentResponseDto.PaymentRefundResponse(false, "")
        }
    }

    @Transactional
    override fun refundPayment() {
        val userList =
            userDao.findNotMatchedUserInMeetingTeam(
                meetingTeamDao.findNotMatchedMaleMeetingTeam() +
                    meetingTeamDao.findNotMatchedFeMaleMeetingTeam()
            )

        userList.map { user ->
            val payment = paymentRepository.findByUser(user) ?: throw PaymentNotFoundException()
            try {
                refundPaymentByPortOne(payment)
                payment.status = PaymentStatus.REFUND
            } catch (e: ExternalApiFailedException) {
                payment.status = PaymentStatus.REFUND_FAILED
            }
        }
    }

    override fun verifyPayment(userUUID: UUID): PaymentResponseDto.PaymentRequestResponse {
        // 미팅팀이 없으면, 신청하기 버튼
        val user = userDao.findUserWithMeetingTeam(userUUID) ?: throw UserNotFoundException()
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

    fun checkPaymentByPortOne(impUid: String): PortOneResponseDto.SingleHistoryResponse {
        val restTemplate = RestTemplateBuilder()
            .errorHandler(restTemplateErrorHandler)
            .build()

        val header =
            HttpHeaders().apply {
                set("Authorization", findAccessToken())
                set("Content-Type", "application/json")
            }

        val requestEntity = HttpEntity(null, header)

        val uri = url + "/payments/" + impUid

        val responseEntity: ResponseEntity<PortOneResponseDto.SingleHistoryResponse> =
            restTemplate.exchange(
                uri,
                HttpMethod.GET,
                requestEntity,
                PortOneResponseDto.SingleHistoryResponse::class.java
            )

        return responseEntity.body!!
    }

    fun refundPaymentByPortOne(payment: Payment): PortOneResponseDto.RefundResponse {
        val restTemplate = RestTemplateBuilder()
            .errorHandler(restTemplateErrorHandler)
            .build()

        val header =
            HttpHeaders().apply {
                set("Authorization", findAccessToken())
                set("Content-Type", "application/json")
            }

        val requestBody = PortOneRequestDto.RefundRequest(payment.impUid, 0)

        val requestEntity: RequestEntity<PortOneRequestDto.RefundRequest> =
            RequestEntity.post(URI.create(url + "/payments/cancel"))
                .headers(header)
                .body(requestBody)

        val responseEntity =
            restTemplate.exchange(requestEntity, PortOneResponseDto.RefundResponse::class.java)
        return responseEntity.body!!
    }

    fun findAccessToken(): String {
        val restTemplate = RestTemplateBuilder()
            .errorHandler(restTemplateErrorHandler)
            .build()

        val header = HttpHeaders().apply { set("Content-Type", "application/json") }

        val requestBody = PortOneRequestDto.AccessTokenRequest(impKey, impSecret)

        val requestEntity: RequestEntity<PortOneRequestDto.AccessTokenRequest> =
            RequestEntity.post(URI.create(url + "/users/getToken"))
                .headers(header)
                .body(requestBody)

        val responseEntity =
            restTemplate.exchange(requestEntity, PortOneResponseDto.AccessTokenResponse::class.java)
        val responseBody = responseEntity.body!!
        return "Bearer " + responseBody.response!!.access_token
    }
}
