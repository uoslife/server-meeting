package uoslife.servermeeting.meetingteam.service.impl

import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import uoslife.servermeeting.meetingteam.dao.PaymentDao
import uoslife.servermeeting.meetingteam.dto.request.PayappRequestDto
import uoslife.servermeeting.meetingteam.dto.response.PayappResponseDto
import uoslife.servermeeting.meetingteam.entity.Payment
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.exception.*
import uoslife.servermeeting.meetingteam.repository.PaymentRepository
import uoslife.servermeeting.meetingteam.service.PaymentService
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.exception.UserNotFoundException
import uoslife.servermeeting.user.repository.UserRepository
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*

@Service
@Qualifier("PayappService")
class PayappService(
    private val userRepository: UserRepository,
    private val paymentRepository: PaymentRepository,
    private val paymentDao: PaymentDao,
    @Value("\${payapp.api.domain}") private val domain: String,
    @Value("\${payapp.api.url}") private val apiUrl: String,
    @Value("\${payapp.api.cmd.requestpayment}") private val requestPaymentCmd: String,
    @Value("\${payapp.api.userid}") private val userId: String,
    @Value("\${payapp.api.goodname}") private val goodName: String,
    @Value("\${payapp.api.price.single}") private val singlePrice: Int,
    @Value("\${payapp.api.price.triple}") private val triplePrice: Int,
    @Value("\${payapp.api.feedbackurl}") private val feedbackUrl: String,
    @Value("\${payapp.api.link.key}") private val linkKey: String,
    @Value("\${payapp.api.link.value}") private val linkValue: String,
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

        // payment 생성
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
        if (payment.status!!.equals(PaymentStatus.COMPLETE_PAYMENT)) {
            throw AlreadyExistsPaymentException()
        }

        // 결제 요청
        val payappRequestStatusResponse = requestPaymentByPayApp(payment)

        // 결제 여부에 따라 PaymentStatus 리턴
        if (payappRequestStatusResponse.state == 1) {
            paymentDao.updatePaymentByRequest(
                payment,
                payappRequestStatusResponse,
                PaymentStatus.REQUEST
            )
        } else {
            paymentDao.updatePaymentByRequest(
                payment,
                payappRequestStatusResponse,
                PaymentStatus.CANCEL_REQUEST
            )
        }

        return payappRequestStatusResponse
    }

    fun requestPaymentByPayApp(payment: Payment): PayappResponseDto.PayappRequestStatusResponse {
        val restTemplate = RestTemplate()

        val header =
            HttpHeaders().apply {
                set("Accept", "text/html,application/xhtml+xml,*/*")
                set("Host", domain)
                set("Accept-Language", "ko-KR")
                set("Content-Type", "application/x-www-form-urlencoded")
            }

        val requestMap: Map<String, String?> = mapOf(
            "cmd" to requestPaymentCmd,
            "userid" to userId,
            "goodname" to goodName,
            "price" to payment.price.toString(),
            "recvphone" to payment.user!!.phoneNumber,
            "feedbackurl" to feedbackUrl,
            "var1" to payment.var1,
            "var2" to payment.var2
        )

        val entity = HttpEntity(mapToQueryString(requestMap), header)
        val response = restTemplate.postForObject(apiUrl, entity, String::class.java)

        val resposneMap = queryStringToMap(URLDecoder.decode(response, "UTF-8"))

        return PayappResponseDto.PayappRequestStatusResponse(
            state = resposneMap.get("state")?.toInt() ?: 0,
            errorMessage = resposneMap.get("errorMessage") ?: "",
            mulNo = resposneMap.get("mul_no")?.toInt() ?: 0,
            payUrl = resposneMap.get("payurl") ?: "",
            qrUrl = resposneMap.get("qrurl") ?: "",
        )
    }

    @Transactional
    override fun checkPayment(request: PayappRequestDto.PayappCheckStatusRequest) {
        require (userId == request.userid &&
            linkKey == request.linkkey &&
            linkValue == request.linkval) {
            throw PaymentInformationInvalidException()
        }

        val payment = paymentDao.selectPaymentByMulNoAndVar(
            request.mulNo,
            request.var1,
            request.var2
        ) ?: throw PaymentNotFoundException()

        require(payment.price != request.price) {
            throw PaymentInformationInvalidException()
        }

        paymentDao.updatePaymentByCheck(payment,
            when(request.payState) {
                1 -> PaymentStatus.REQUEST
                4 -> PaymentStatus.COMPLETE_PAYMENT
                8, 16, 32 -> PaymentStatus.CANCEL_REQUEST
                9, 64 -> PaymentStatus.REQUEST
                10 -> PaymentStatus.WAITING_PAYMENT
                70, 71 -> PaymentStatus.CANCEL_PARTIAL
                else -> throw PaymentInformationInvalidException()
            }
        )
    }

    @Transactional
    override fun refundPaymentById(): Unit {
        TODO("id 기반으로 특정 유저만 결제 취소 api 구현(부분 취소)")
    }

    @Transactional
    override fun refundPayment(): Unit {
        TODO("매칭이 안됐을 경우 결제 취소 api 구현(부분 취소)")
    }

    fun mapToQueryString(map: Map<String, String?>): String {
        return map.entries.joinToString("&") { (key, value) ->
            "${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value, "UTF-8")}"
        }
    }

    fun queryStringToMap(queryString: String): MutableMap<String, String> {
        val paramMap = mutableMapOf<String, String>()

        queryString.split("&").forEach { pair ->
            val (key, value) = pair.split("=")
            paramMap[key] = value
        }
        return paramMap
    }

}
