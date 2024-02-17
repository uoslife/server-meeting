package uoslife.servermeeting.meetingteam.service.impl

import jakarta.transaction.Transactional
import java.net.URLDecoder
import java.util.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import uoslife.servermeeting.meetingteam.dao.PaymentDao
import uoslife.servermeeting.meetingteam.dto.response.PayappResponseDto
import uoslife.servermeeting.meetingteam.entity.Payment
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.exception.AlreadyExistsPaymentException
import uoslife.servermeeting.meetingteam.exception.MeetingTeamNotFoundException
import uoslife.servermeeting.meetingteam.exception.PhoneNumberNotFoundException
import uoslife.servermeeting.meetingteam.repository.PaymentRepository
import uoslife.servermeeting.meetingteam.service.PaymentService
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.exception.UserNotFoundException
import uoslife.servermeeting.user.repository.UserRepository

@Service
@Qualifier("PayappService")
class PayappService(
    private val userRepository: UserRepository,
    private val paymentRepository: PaymentRepository,
    private val paymentDao: PaymentDao,
    @Value("\${payapp.api.url}") private val apiUrl: String,
    @Value("\${payapp.api.cmd.spendpayment}") private val spendPaymentCmd: String,
    @Value("\${payapp.api.userid}") private val userId: String,
    @Value("\${payapp.api.goodname}") private val goodName: String,
    @Value("\${payapp.api.price.single}") private val singlePrice: Int,
    @Value("\${payapp.api.price.triple}") private val triplePrice: Int,
    @Value("") private val feedbackUrl: String,
) : PaymentService {
    @Transactional
    override fun requestPayment(userUUID: UUID): PayappResponseDto.PayappRequestStatusResponse {
        // user가 없거나 user가 소속된 팀, 전화번호 없는 경우 예외 처리
        val user: User =
            userRepository.findByIdOrNull(userUUID)?.also { user ->
                user.team ?: throw MeetingTeamNotFoundException()
                user.phoneNumber ?: throw PhoneNumberNotFoundException()
            }
                ?: throw UserNotFoundException()

        var payment: Payment =
            paymentRepository.findByUser(user)
                ?: Payment.createPayment(
                    user,
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    when (user.team!!.type) {
                        TeamType.SINGLE -> singlePrice
                        TeamType.TRIPLE -> triplePrice
                    }
                )

        // 이미 결제한 경우 중복 결제임으로 예외 처리
        if (payment.status!!.equals(PaymentStatus.PAID)) {
            throw AlreadyExistsPaymentException()
        }

        // 결제 요청
        val payappRequestStatusResponse = requestPaymentByPayApp(payment)

        // 결제 여부에 따라 PaymentStatus 리턴
        if (payappRequestStatusResponse.state == 1) {
            paymentDao.updatePayment(payment, payappRequestStatusResponse, PaymentStatus.REQUESTED)
        } else {
            paymentDao.updatePayment(
                payment,
                payappRequestStatusResponse,
                PaymentStatus.FAILED_REQUEST
            )
        }

        return payappRequestStatusResponse
    }

    fun requestPaymentByPayApp(payment: Payment): PayappResponseDto.PayappRequestStatusResponse {
        val restTemplate = RestTemplate()

        val header =
            HttpHeaders().apply {
                contentType = MediaType.APPLICATION_FORM_URLENCODED
                acceptCharset = listOf(Charsets.UTF_8)
            }

        val requestBody: MultiValueMap<String, Any> = LinkedMultiValueMap()
        requestBody.add("cmd", spendPaymentCmd)
        requestBody.add("userid", userId)
        requestBody.add("goodname", goodName)
        requestBody.add("price", payment.price.toString())
        requestBody.add("recvphone", payment.user!!.phoneNumber)
        requestBody.add("feedbackurl", feedbackUrl)
        requestBody.add("var1", payment.var1)
        requestBody.add("var2", payment.var2)

        val entity = HttpEntity(requestBody, header)
        val response = restTemplate.postForObject(apiUrl, entity, String::class.java)

        val decodedResponseString = URLDecoder.decode(response, "UTF-8")

        val queryParams =
            UriComponentsBuilder.fromUriString(decodedResponseString).build().queryParams

        return PayappResponseDto.PayappRequestStatusResponse(
            state = queryParams.getFirst("state")?.toInt() ?: 0,
            errorMessage = queryParams.getFirst("errorMessage") ?: "",
            mulNo = queryParams.getFirst("mul_no")?.toInt() ?: 0,
            payUrl = queryParams.getFirst("payurl") ?: "",
            qrUrl = queryParams.getFirst("qrurl") ?: "",
        )
    }

    override fun refundPaymentById(): Unit {
        TODO("id 기반으로 특정 유저만 결제 취소 api 구현(부분 취소)")
    }

    @Transactional
    override fun refundPayment(): Unit {
        TODO("매칭이 안됐을 경우 결제 취소 api 구현(부분 취소)")
    }
}
