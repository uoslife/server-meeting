package uoslife.servermeeting.meetingteam.api

import io.swagger.v3.oas.annotations.tags.Tag
import java.util.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uoslife.servermeeting.meetingteam.dto.response.PaymentStatusResponse
import uoslife.servermeeting.meetingteam.service.PaymentService

@RestController
@RequestMapping("/api/payment")
@Tag(name = "Payment", description = "결제 API")
class PaymentApi(@Qualifier("PayappService") private val paymentService: PaymentService) {

    @PostMapping("/spend")
    fun spendPayment(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<PaymentStatusResponse> {
        val userUUID = UUID.fromString(userDetails.username)

        val paymentStatusResponse =
            PaymentStatusResponse(paymentStatus = paymentService.spendPayment(userUUID))
        return ResponseEntity.status(HttpStatus.OK).body(paymentStatusResponse)
    }

    @PostMapping("/refund/request") fun refundPaymentById(): Unit {}

    @PostMapping("/refund") fun refundPayment(): Unit {}
}
