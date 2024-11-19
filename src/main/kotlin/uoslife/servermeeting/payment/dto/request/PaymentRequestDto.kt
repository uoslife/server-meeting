package uoslife.servermeeting.payment.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import uoslife.servermeeting.payment.entity.enums.PayMethod
import uoslife.servermeeting.payment.entity.enums.PaymentGateway

class PaymentRequestDto {
    data class PaymentRequestRequest(
        @Schema(description = "결제 플랫폼", example = "WELCOME_PAYMENTS", nullable = false)
        @field:NotNull
        val pg: PaymentGateway,
        @Schema(description = "결제 수단", example = "CARD", nullable = false)
        @field:NotNull
        val payMethod: PayMethod,
    )

    data class PaymentCheckRequest(
        @Schema(description = "결제 고유 번호", nullable = false) @field:NotBlank val impUid: String,
    )

    data class PaymentRefundRequest(
        @field:NotBlank
        @Schema(description = "전화번호", example = "01012341234", nullable = false)
        val phoneNumber: String,
    )
}
