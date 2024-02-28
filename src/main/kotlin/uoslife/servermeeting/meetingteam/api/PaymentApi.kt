package uoslife.servermeeting.meetingteam.api

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
    @PostMapping
    fun requestPayment(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestBody paymentRequestPaymentRequest: PaymentRequestDto.PaymentRequestRequest
    ): ResponseEntity<PaymentResponseDto.PaymentRequestResponse> {
        val userUUID = UUID.fromString(userDetails.username)

        return ResponseEntity.status(HttpStatus.OK)
            .body(paymentService.requestPayment(userUUID, paymentRequestPaymentRequest))
    }

    @PostMapping("/check")
    fun checkPayment(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestBody paymentCheckRequest: PaymentRequestDto.PaymentCheckRequest
    ): ResponseEntity<PaymentResponseDto.PaymentCheckResponse> {
        val userUUID = UUID.fromString(userDetails.username)

        return ResponseEntity.status(HttpStatus.OK)
            .body(paymentService.checkPayment(userUUID, paymentCheckRequest))
    }
}
