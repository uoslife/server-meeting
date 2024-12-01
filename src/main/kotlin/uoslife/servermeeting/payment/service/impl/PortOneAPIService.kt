package uoslife.servermeeting.payment.service.impl

import java.time.Duration
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import uoslife.servermeeting.global.auth.exception.ExternalApiFailedException
import uoslife.servermeeting.global.external.PaymentClient
import uoslife.servermeeting.payment.dto.request.PortOneRequestDto
import uoslife.servermeeting.payment.dto.response.PortOneResponseDto

@Service
class PortOneAPIService(
    private val paymentClient: PaymentClient,
    private val redisTemplate: RedisTemplate<String, String>,
    @Value("\${portone.api.imp.key}") private val impKey: String,
    @Value("\${portone.api.imp.secret}") private val impSecret: String,
) {
    companion object {
        const val ACCESS_TOKEN_KEY = "PORTONE_ACCESS_KEY"
        const val SUCCESS_CODE = 0
        const val TOKEN_VALID_TIME = 60L // 포트원 1800초 보다 축소
    }
    private fun getAccessToken(): String {
        val accessKey = redisTemplate.opsForValue().get(ACCESS_TOKEN_KEY)
        if (accessKey != null) {
            return accessKey
        }

        val accessTokenResponse = requestAccessToken()
        checkResponseCode(accessTokenResponse)
        val newAccessToken = accessTokenResponse.response!!.access_token
        redisTemplate
            .opsForValue()
            .set(ACCESS_TOKEN_KEY, newAccessToken, Duration.ofSeconds(TOKEN_VALID_TIME))
        return newAccessToken
    }

    fun refreshAccessToken() {
        val accessTokenResponse = requestAccessToken()
        checkResponseCode(accessTokenResponse)
        val newAccessToken = accessTokenResponse.response!!.access_token
        redisTemplate
            .opsForValue()
            .set(ACCESS_TOKEN_KEY, newAccessToken, Duration.ofSeconds(TOKEN_VALID_TIME))
    }

    private fun requestAccessToken(): PortOneResponseDto.AccessTokenResponse {
        return paymentClient.getAccessToken(
            PortOneRequestDto.AccessTokenRequest(imp_key = impKey, imp_secret = impSecret)
        )
    }

    fun checkPayment(impUid: String): PortOneResponseDto.SingleHistoryResponse {
        val accessToken = getAccessToken()
        return paymentClient.checkPayment(accessToken = accessToken, impUid = impUid)
    }

    fun refundPayment(impUid: String?, price: Number?): PortOneResponseDto.RefundResponse {
        val accessToken = getAccessToken()
        return paymentClient.refundPayment(
            accessToken,
            PortOneRequestDto.RefundRequest(imp_uid = impUid, amount = price)
        )
    }

    fun findPaymentByMID(
        merchantUid: String,
    ): PortOneResponseDto.SingleHistoryResponse {
        val accessToken = getAccessToken()
        return paymentClient.findPaymentByMID(accessToken = accessToken, merchantUid = merchantUid)
    }

    private fun checkResponseCode(accessTokenResponse: PortOneResponseDto.AccessTokenResponse) {
        if (accessTokenResponse.code == SUCCESS_CODE) return
        throw ExternalApiFailedException()
    }
}
