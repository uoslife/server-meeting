package uoslife.servermeeting.meetingteam.service.impl

import jakarta.transaction.Transactional
import java.util.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import uoslife.servermeeting.meetingteam.dto.request.PaymentRequestDto
import uoslife.servermeeting.meetingteam.dto.request.PortOneRequestDto
import uoslife.servermeeting.meetingteam.dto.response.PaymentResponseDto
import uoslife.servermeeting.meetingteam.dto.response.PortOneResponseDto
import uoslife.servermeeting.meetingteam.entity.Payment
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.exception.AccessTokenNotFoundException
import uoslife.servermeeting.meetingteam.exception.MeetingTeamNotFoundException
import uoslife.servermeeting.meetingteam.exception.PaymentNotFoundException
import uoslife.servermeeting.meetingteam.exception.PhoneNumberNotFoundException
import uoslife.servermeeting.meetingteam.repository.PaymentRepository
import uoslife.servermeeting.meetingteam.service.PaymentService
import uoslife.servermeeting.user.exception.UserNotFoundException
import uoslife.servermeeting.user.repository.UserRepository
import java.lang.Exception
import java.net.URI

@Service
@Qualifier("PortOneService")
class PortOneService(
    private val userRepository: UserRepository,
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
        request: PaymentRequestDto.PaymentRequestRequest
    ): PaymentResponseDto.PaymentRequestResponse {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()
        val team = user.team ?: throw MeetingTeamNotFoundException()
        val phoneNumber = user.phoneNumber ?: throw PhoneNumberNotFoundException()

        val payment =
            Payment.createPayment(
                user = user,
                pg = request.pg,
                payMethod = request.payMethod,
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
            phoneNumber
        )
    }

    @Transactional
    override fun checkPayment(
        userUUID: UUID,
        request: PaymentRequestDto.PaymentCheckRequest
    ): PaymentResponseDto.PaymentCheckResponse {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()
        val payment = paymentRepository.findByUser(user) ?: throw PaymentNotFoundException()

        val payResult = checkPaymentByPortOne(request.impUid)

        if (payResult.code == 0) {
            payment.impUid = request.impUid
            payment.status = PaymentStatus.SUCCESS
            return PaymentResponseDto.PaymentCheckResponse(true, "")
        }
        else {
            payment.status = PaymentStatus.FAILED
            return PaymentResponseDto.PaymentCheckResponse(false, payResult.message!!)
        }
    }

    fun checkPaymentByPortOne(impUid: String): PortOneResponseDto.PaymentSingleHistoryResponse{
        val restTemplate = RestTemplate()

        val header =
            HttpHeaders().apply {
                set("Authorization", findAccessToken())
                set("Content-Type", "application/json")
            }

        val uri = url + "/payments/" + impUid

        val responseEntity: ResponseEntity<PortOneResponseDto.PaymentSingleHistoryResponse> = restTemplate.exchange(
            uri,
            HttpMethod.GET,
            null, // 요청 바디가 필요 없는 경우 null 전달
            PortOneResponseDto.PaymentSingleHistoryResponse::class.java
        )

        return responseEntity.body!!
    }
    fun findAccessToken(): String{
        val restTemplate = RestTemplate()

        val header =
            HttpHeaders().apply {
                set("Content-Type", "application/json")
            }

        val requestBody = PortOneRequestDto.AccessTokenRequest(impKey, impSecret)

        val requestEntity: RequestEntity<PortOneRequestDto.AccessTokenRequest> = RequestEntity
            .post(URI.create(url + "/users/getToken"))
            .headers(header)
            .body(requestBody)

        val responseEntity = restTemplate.exchange(requestEntity, PortOneResponseDto.AccessTokenResponse::class.java)
        val responseBody = responseEntity.body!!

        if (responseBody.code != 0) {
            throw AccessTokenNotFoundException()
        }
        return responseBody.response!!.access_token!!
    }

    override fun refundPaymentById() {
        TODO("특정 유저 결제 취소")
    }

    override fun refundPayment() {
        TODO("비매칭인원 전체 결제 취소")
    }
}
