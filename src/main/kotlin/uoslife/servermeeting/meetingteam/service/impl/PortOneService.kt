package uoslife.servermeeting.meetingteam.service.impl

import jakarta.transaction.Transactional
import java.net.URI
import java.util.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import uoslife.servermeeting.meetingteam.dao.MeetingTeamDao
import uoslife.servermeeting.meetingteam.dto.request.PaymentRequestDto
import uoslife.servermeeting.meetingteam.dto.request.PortOneRequestDto
import uoslife.servermeeting.meetingteam.dto.response.PaymentResponseDto
import uoslife.servermeeting.meetingteam.dto.response.PortOneResponseDto
import uoslife.servermeeting.meetingteam.entity.Payment
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.exception.*
import uoslife.servermeeting.meetingteam.repository.PaymentRepository
import uoslife.servermeeting.meetingteam.service.PaymentService
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

        if (paymentRepository.existsByUser(user)) throw UserAlreadyHavePaymentException()

        val team = user.team ?: throw MeetingTeamNotFoundException()
        val phoneNumber = user.phoneNumber ?: throw PhoneNumberNotFoundException()

        val payment =
            Payment.createPayment(
                user = user,
                pg = paymentRequestPaymentRequest.pg,
                payMethod = paymentRequestPaymentRequest.payMethod,
                marchantUid = UUID.randomUUID().toString(),
                price =
                    when (team.type) {
                        TeamType.SINGLE -> singlePrice
                        TeamType.TRIPLE -> triplePrice
                    },
                status = PaymentStatus.REQUEST,
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

        val response = checkPaymentByPortOne(paymentCheckRequest.impUid)

        if (response.code == 0) {
            payment.impUid = paymentCheckRequest.impUid
            payment.status = PaymentStatus.SUCCESS
            return PaymentResponseDto.PaymentCheckResponse(true, "")
        } else {
            payment.status = PaymentStatus.FAILED
            return PaymentResponseDto.PaymentCheckResponse(false, response.message!!)
        }
    }

    fun checkPaymentByPortOne(impUid: String): PortOneResponseDto.SingleHistoryResponse {
        val restTemplate = RestTemplate()
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

    @Transactional
    override fun refundPaymentByPhoneNumber(
        phoneNumber: String
    ): PaymentResponseDto.PaymentRefundResponse {
        val user = userRepository.findByPhoneNumber(phoneNumber) ?: throw UserNotFoundException()
        val payment = paymentRepository.findByUser(user) ?: throw PaymentNotFoundException()

        if (!payment.status.equals(PaymentStatus.SUCCESS)) {
            throw PaymentInValidException()
        }

        val response = refundPaymentByPortOne(payment)

        if (response.code == 0) {
            payment.status = PaymentStatus.REFUND
            return PaymentResponseDto.PaymentRefundResponse(true, "")
        } else {
            payment.status = PaymentStatus.FAILED
            return PaymentResponseDto.PaymentRefundResponse(false, response.message!!)
        }
    }

    fun refundPaymentByPortOne(payment: Payment): PortOneResponseDto.RefundResponse {
        val restTemplate = RestTemplate()

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

    @Transactional
    override fun refundPayment(): PaymentResponseDto.PaymentNotMatchingRefundResponse {
        val userList =
            userDao.findNotMatchedUserInMeetingTeam(
                meetingTeamDao.findNotMatchedMaleMeetingTeam() +
                    meetingTeamDao.findNotMatchedFeMaleMeetingTeam()
            )

        val refundList =
            userList.map { user ->
                val payment = paymentRepository.findByUser(user) ?: throw PaymentNotFoundException()
                val response = refundPaymentByPortOne(payment)

                if (response.code == 0) {
                    payment.status = PaymentStatus.REFUND
                    PaymentResponseDto.PaymentRefundResponse(true, "")
                } else {
                    payment.status = PaymentStatus.FAILED
                    PaymentResponseDto.PaymentRefundResponse(false, response.message!!)
                }
            }

        val successCount = refundList.count { it.cancelSuccess }
        val failedCount = refundList.size - successCount

        return PaymentResponseDto.PaymentNotMatchingRefundResponse(
            successCount,
            failedCount,
            refundList
        )
    }

    fun findAccessToken(): String {
        val restTemplate = RestTemplate()

        val header = HttpHeaders().apply { set("Content-Type", "application/json") }

        val requestBody = PortOneRequestDto.AccessTokenRequest(impKey, impSecret)

        val requestEntity: RequestEntity<PortOneRequestDto.AccessTokenRequest> =
            RequestEntity.post(URI.create(url + "/users/getToken"))
                .headers(header)
                .body(requestBody)

        val responseEntity =
            restTemplate.exchange(requestEntity, PortOneResponseDto.AccessTokenResponse::class.java)
        val responseBody = responseEntity.body!!

        if (responseBody.code != 0) {
            throw AccessTokenNotFoundException()
        }
        return "Bearer " + responseBody.response!!.access_token
    }
}
