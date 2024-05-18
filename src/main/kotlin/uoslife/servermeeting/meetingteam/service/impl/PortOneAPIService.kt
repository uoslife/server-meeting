package uoslife.servermeeting.meetingteam.service.impl

import org.springframework.stereotype.Service
import uoslife.servermeeting.global.external.PaymentClient
import uoslife.servermeeting.meetingteam.dto.request.PortOneRequestDto
import uoslife.servermeeting.meetingteam.dto.response.PortOneResponseDto

@Service
class PortOneAPIService(private val paymentClient: PaymentClient) {
    fun getAccessToken(impKey: String, impSecret: String): PortOneResponseDto.AccessTokenResponse {
        return paymentClient.getAccessToken(
            PortOneRequestDto.AccessTokenRequest(imp_key = impKey, imp_secret = impSecret)
        )
    }

    fun checkPayment(
        accessToken: String,
        impUid: String
    ): PortOneResponseDto.SingleHistoryResponse {
        return paymentClient.checkPayment(accessToken = accessToken, impUid = impUid)
    }

    fun refundPayment(
        accessToken: String,
        impUid: String?,
        price: Number?
    ): PortOneResponseDto.RefundResponse {
        return paymentClient.refundPayment(
            accessToken,
            PortOneRequestDto.RefundRequest(imp_uid = impUid, amount = price)
        )
    }
}
