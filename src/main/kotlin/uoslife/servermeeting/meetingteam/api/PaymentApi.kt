package uoslife.servermeeting.meetingteam.api

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uoslife.servermeeting.meetingteam.service.PaymentService

@RestController
@RequestMapping("/api/payment")
@Tag(name = "Payment", description = "결제 API")
class PaymentApi(
    @Qualifier("PayappService") private val paymentService: PaymentService
) {

    @PostMapping("/spend")
    fun spendPayment(): Unit {

    }

    @PostMapping("/cancel/request")
    fun spendPaymentRequest(): Unit {

    }

    @PostMapping("/cancel")
    fun cancelPayment(): Unit {

    }
}

