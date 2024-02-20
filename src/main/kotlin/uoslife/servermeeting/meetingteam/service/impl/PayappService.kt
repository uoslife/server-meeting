package uoslife.servermeeting.meetingteam.service.impl

import jakarta.transaction.Transactional
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import uoslife.servermeeting.meetingteam.dao.PaymentDao
import uoslife.servermeeting.meetingteam.dto.request.PayappRequestDto
import uoslife.servermeeting.meetingteam.dto.response.PayappResponseDto
import uoslife.servermeeting.meetingteam.entity.Payment
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.exception.*
import uoslife.servermeeting.meetingteam.repository.PaymentRepository
import uoslife.servermeeting.meetingteam.service.PaymentService
import uoslife.servermeeting.user.dao.UserDao
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.exception.UserNotFoundException
import uoslife.servermeeting.user.repository.UserRepository

@Service
@Qualifier("PayappService")
class PayappService(
    private val userRepository: UserRepository,
    private val paymentRepository: PaymentRepository,
    private val userDao: UserDao,
    private val paymentDao: PaymentDao,
    @Value("\${payapp.api.domain}") private val domain: String,
    @Value("\${payapp.api.url}") private val apiUrl: String,
    @Value("\${payapp.api.cmd.requestpayment}") private val requestPaymentCmd: String,
    @Value("\${payapp.api.cmd.cancelpayment}") private val cancelPaymentCmd: String,
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
        val payappRequestStatusResponse = requestPaymentInPayApp(payment)

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

    fun requestPaymentInPayApp(payment: Payment): PayappResponseDto.PayappRequestStatusResponse {
        val restTemplate = RestTemplate()

        val header =
            HttpHeaders().apply {
                set("Accept", "text/html,application/xhtml+xml,*/*")
                set("Host", domain)
                set("Accept-Language", "ko-KR")
                set("Content-Type", "application/x-www-form-urlencoded")
            }

        val requestMap: Map<String, String?> =
            mapOf(
                "cmd" to requestPaymentCmd,
                "userid" to userId,
                "goodname" to goodName,
                "price" to payment.price.toString(),
                "recvphone" to payment.user!!.phoneNumber,
                "feedbackurl" to feedbackUrl,
                "identifier1" to payment.identifier1,
                "identifier2" to payment.identifier2
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
        require(
            userId == request.userid && linkKey == request.linkkey && linkValue == request.linkval
        ) {
            throw PaymentInformationInvalidException()
        }

        val payment =
            paymentDao.selectPaymentByMulNoAndVar(request.mulNo, request.identifier1, request.identifier2)
                ?: throw PaymentNotFoundException()

        require(payment.price != request.price) { throw PaymentInformationInvalidException() }

        paymentDao.updatePaymentByCheck(
            payment,
            when (request.payState) {
                1 -> PaymentStatus.REQUEST
                4 -> PaymentStatus.COMPLETE_PAYMENT
                8,
                16,
                32 -> PaymentStatus.CANCEL_REQUEST
                9,
                64 -> PaymentStatus.REQUEST
                10 -> PaymentStatus.WAITING_PAYMENT
                70,
                71 -> PaymentStatus.CANCEL_PARTIAL_REFUND
                else -> throw PaymentInformationInvalidException()
            }
        )
    }

    @Transactional
    override fun refundPaymentById(userUUID: UUID): PayappResponseDto.PayappCancelStatusResponse {
        val user: User =
            userRepository.findByIdOrNull(userUUID)?.also { user ->
                user.team ?: throw MeetingTeamNotFoundException()
            }
                ?: throw UserNotFoundException()

        val price =
            when (user.team!!.type) {
                TeamType.SINGLE -> singlePrice
                TeamType.TRIPLE -> triplePrice
            }

        var payment: Payment =
            paymentRepository.findByUser(user) ?: throw PaymentNotFoundException()

        val payappCancelStatusResponse = refundPaymentByIdInPayApp(payment, price)

        if (payappCancelStatusResponse.state == 1) {
            paymentDao.updatePaymentByCancel(payment, PaymentStatus.CANCEL_PARTIAL_REFUND)
        } else {
            paymentDao.updatePaymentByCancel(payment, PaymentStatus.FAILED_REFUND)
        }

        return payappCancelStatusResponse
    }

    @Transactional
    override fun refundPayment(): PayappResponseDto.PayappNotMatchingCancelResponse {
        val users = userDao.selctNotMatchedUser()

        val payappCanelStatusResponseList =
            users.map { user ->
                val price =
                    when (user.team!!.type) {
                        TeamType.SINGLE -> singlePrice
                        TeamType.TRIPLE -> triplePrice
                    }
                val payappCanelStatusResponse = refundPaymentByIdInPayApp(user.payment!!, price)
                payappCanelStatusResponse
            }

        val successCount = payappCanelStatusResponseList.sumOf { it.state }
        val failedCount = payappCanelStatusResponseList.size - successCount

        return PayappResponseDto.PayappNotMatchingCancelResponse(
            successCount = successCount,
            failedCount = failedCount,
            payappCancelStatusResponseList = payappCanelStatusResponseList
        )
    }

    fun refundPaymentByIdInPayApp(
        payment: Payment,
        price: Int
    ): PayappResponseDto.PayappCancelStatusResponse {
        val restTemplate = RestTemplate()

        val header =
            HttpHeaders().apply {
                set("Accept", "text/html,application/xhtml+xml,*/*")
                set("Host", domain)
                set("Accept-Language", "ko-KR")
                set("Content-Type", "application/x-www-form-urlencoded")
            }

        val requestMap: Map<String, String?> =
            mapOf(
                "cmd" to cancelPaymentCmd,
                "userid" to userId,
                "linkkey" to linkKey,
                "mul_no" to payment.mulNo.toString(),
                "cancelmemo" to "cancel payment to " + payment.user!!.id,
                "dpname" to "시대생",
                "partcancel" to "1",
                "cancelprice" to price.toString(),
            )

        val entity = HttpEntity(mapToQueryString(requestMap), header)
        val response = restTemplate.postForObject(apiUrl, entity, String::class.java)

        val resposneMap = queryStringToMap(URLDecoder.decode(response, "UTF-8"))

        return PayappResponseDto.PayappCancelStatusResponse(
            state = resposneMap.get("state")?.toInt() ?: 0,
            errorMessage = resposneMap.get("errorMessage") ?: "",
            crDpname = resposneMap.get("cr_dpname") ?: "",
            partcancel = resposneMap.get("partcancel") ?: "",
            paybackprice = resposneMap.get("paybackprice")?.toInt() ?: 0,
            partprice = resposneMap.get("partprice")?.toInt() ?: 0,
            cancelTaxable = resposneMap.get("cancel_taxable")?.toInt() ?: 0,
            cancelVat = resposneMap.get("cancel_vat")?.toInt() ?: 0,
            cancelTaxfree = resposneMap.get("cancel_taxfree")?.toInt() ?: 0,
            paybackbank = resposneMap.get("paybackbank") ?: "",
        )
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
