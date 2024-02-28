package uoslife.servermeeting.meetingteam.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import java.util.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uoslife.servermeeting.meetingteam.dto.request.PaymentRequestDto
import uoslife.servermeeting.meetingteam.dto.response.PaymentResponseDto
import uoslife.servermeeting.meetingteam.service.PaymentService

@RestController
@RequestMapping("/api/payment")
@Tag(name = "Payment", description = "결제 API")
class PaymentApi(@Qualifier("PortOneService") private val paymentService: PaymentService) {

    @Operation(summary = "결제 요청 API", description = "결제를 요청하여 주문 번호를 반환합니다.")
    @ApiResponse(
        responseCode = "200",
        description = "marchantUid 정상 반환",
    )
    @PostMapping("/request")
    fun requestPayment(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestBody paymentRequestPaymentRequest: PaymentRequestDto.PaymentRequestRequest
    ): ResponseEntity<PaymentResponseDto.PaymentRequestResponse> {
        val userUUID = UUID.fromString(userDetails.username)

        return ResponseEntity.status(HttpStatus.OK)
            .body(paymentService.requestPayment(userUUID, paymentRequestPaymentRequest))
    }

    @Operation(summary = "결제 검증 API", description = "결제가 되었는지 검증합니다.")
    @ApiResponse(
        responseCode = "200",
        description = "결제 완료",
    )
    @PostMapping("/check")
    fun checkPayment(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestBody paymentCheckRequest: PaymentRequestDto.PaymentCheckRequest
    ): ResponseEntity<PaymentResponseDto.PaymentCheckResponse> {
        val userUUID = UUID.fromString(userDetails.username)

        return ResponseEntity.status(HttpStatus.OK)
            .body(paymentService.checkPayment(userUUID, paymentCheckRequest))
    }

    @Operation(summary = "결제 취소 API", description = "특정한 유저에게 환불해줍니다.")
    @ApiResponse(
        responseCode = "200",
        description = "결제 환불 완료",
    )
    @PostMapping("/refund")
    fun refundPaymentById(
        @AuthenticationPrincipal userDetails: UserDetails,
    ): ResponseEntity<PaymentResponseDto.PaymentRefundResponse> {
        val userUUID = UUID.fromString(userDetails.username)

        return ResponseEntity.status(HttpStatus.OK).body(paymentService.refundPaymentById(userUUID))
    }

    @Operation(summary = "매칭 안된 유저 결제 취소 API", description = "매칭이 되지않은 모든 유저에 대해 환불합니다")
    @ApiResponse(
        responseCode = "200",
        description = "환볼 성공/실패 수, 각 환불정보 제공",
    )
    @PostMapping("/refund/match")
    fun refundPayment(): ResponseEntity<PaymentResponseDto.PaymentNotMatchingRefundResponse> {
        return ResponseEntity.status(HttpStatus.OK).body(paymentService.refundPayment())
    }
}
