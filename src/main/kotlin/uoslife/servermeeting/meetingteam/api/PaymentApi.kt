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
import uoslife.servermeeting.meetingteam.dto.request.PayappRequestDto
import uoslife.servermeeting.meetingteam.dto.response.PayappResponseDto
import uoslife.servermeeting.meetingteam.service.PaymentService

@RestController
@RequestMapping("/api/payment")
@Tag(name = "Payment", description = "결제 API")
class PaymentApi(@Qualifier("PayappService") private val paymentService: PaymentService) {

    @Operation(summary = "결제 요청 API", description = "결제 요청 링크를 제공(아직 결제된 것 아님)")
    @ApiResponse(
        responseCode = "200",
        description = "state = 1이면 요청 성공, state = 0이면 요청 실패",
    )
    @PostMapping("/request")
    fun requestPayment(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<PayappResponseDto.PayappRequestStatusResponse> {
        val userUUID = UUID.fromString(userDetails.username)

        val payappRequestStatusResponse = paymentService.requestPayment(userUUID)
        return ResponseEntity.status(HttpStatus.OK).body(payappRequestStatusResponse)
    }

    // TODO: AUTH FITERING OFF
    @Operation(summary = "결제 통보 API", description = "payapp 서버에서 해당 api로 결제 결과 전달")
    @ApiResponse(
        responseCode = "200",
        description = "pay_state를 통해 결제 상태 분류",
    )
    @PostMapping("/check")
    fun checkPayment(
        @RequestBody payappCheckStatusRequest: PayappRequestDto.PayappCheckStatusRequest
    ): ResponseEntity<String> {
        paymentService.checkPayment(payappCheckStatusRequest)
        return ResponseEntity.ok("SUCCESS")
    }

    @Operation(summary = "결제 취소 API", description = "user 특정하여 결제 취소")
    @ApiResponse(
        responseCode = "200",
        description = "mulNo를 통해 식별",
    )
    @PostMapping("/refund")
    fun refundPaymentById(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<PayappResponseDto.PayappCancelStatusResponse> {
        val userUUID = UUID.fromString(userDetails.username)

        val payappCancelStatusResponse = paymentService.refundPaymentById(userUUID)
        return ResponseEntity.status(HttpStatus.OK).body(payappCancelStatusResponse)
    }

    @Operation(summary = "매칭 안된 유저 결제 취소 API", description = "매칭이 되지않은 모든 유저에 대해 환불")
    @ApiResponse(
        responseCode = "200",
        description = "모든 환불 정보 제공",
    )
    @PostMapping("/refund/all")
    fun refundPayment(): ResponseEntity<PayappResponseDto.PayappNotMatchingCancelResponse> {
        val payappNotMatchingCancelResponse = paymentService.refundPayment()
        return ResponseEntity.status(HttpStatus.OK).body(payappNotMatchingCancelResponse)
    }
}
