package uoslife.servermeeting.global.external

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*
import uoslife.servermeeting.global.config.FeginClientConfig
import uoslife.servermeeting.payment.dto.request.PortOneRequestDto
import uoslife.servermeeting.payment.dto.response.PortOneResponseDto

@FeignClient(
    name = "uoslife-meeting-payment-api",
    url = "\${portone.api.url}",
    configuration = [FeginClientConfig::class]
)
interface PaymentClient {
    @PostMapping("/users/getToken", consumes = ["application/json"])
    fun getAccessToken(
        @RequestBody request: PortOneRequestDto.AccessTokenRequest
    ): PortOneResponseDto.AccessTokenResponse
    @GetMapping("/payments/{impUid}", consumes = ["application/json"])
    fun checkPayment(
        @RequestHeader("Authorization") accessToken: String,
        @RequestParam("impUid") impUid: String
    ): PortOneResponseDto.SingleHistoryResponse

    @PostMapping("/payments/cancel", consumes = ["application/json"])
    fun refundPayment(
        @RequestHeader("Authorization") accessToken: String,
        @RequestBody request: PortOneRequestDto.RefundRequest
    ): PortOneResponseDto.RefundResponse

    @GetMapping("/payments/find/{merchant_uid}/{payment_status}", consumes = ["application/json"])
    fun findPaymentByMID(
        @RequestHeader("Authorization") accessToken: String,
        @PathVariable("merchant_uid") merchantUid: String,
    ): PortOneResponseDto.SingleHistoryResponse
}
