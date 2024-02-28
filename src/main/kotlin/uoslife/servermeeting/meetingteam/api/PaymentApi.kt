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
import uoslife.servermeeting.meetingteam.dto.request.PortOneRequestDto
import uoslife.servermeeting.meetingteam.dto.response.PortOneResponseDto
import uoslife.servermeeting.meetingteam.service.PaymentService

@RestController
@RequestMapping("/api/payment")
@Tag(name = "Payment", description = "결제 API")
class PaymentApi(@Qualifier("PortOneService") private val paymentService: PaymentService) {
    @PostMapping
    fun requestPayment(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestBody portOneRequestPaymentRequest: PortOneRequestDto.PortOneRequestPaymentRequest
    ): ResponseEntity<PortOneResponseDto.PortOneRequestPaymentResponse> {
        val userUUID = UUID.fromString(userDetails.username)

        return ResponseEntity.status(HttpStatus.OK)
            .body(paymentService.requestPayment(userUUID, portOneRequestPaymentRequest))
    }
}
