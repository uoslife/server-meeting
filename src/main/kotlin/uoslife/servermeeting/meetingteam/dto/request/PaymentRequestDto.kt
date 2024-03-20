package uoslife.servermeeting.meetingteam.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import uoslife.servermeeting.meetingteam.entity.enums.PayMethod
import uoslife.servermeeting.meetingteam.entity.enums.PaymentGateway

class PaymentRequestDto {
    data class PaymentRequestRequest(
        @Schema(description = "결제 플랫폼", example = "kakaopay") @field:NotNull val pg: PaymentGateway,
        @Schema(description = "결제 수단", example = "card") @field:NotNull val payMethod: PayMethod,
    )

    data class PaymentCheckRequest(
        @Schema(description = "결제 고유 번호") @field:NotBlank val impUid: String,
    )

    data class PaymentRefundRequest(
        @field:NotBlank
        @Schema(description = "전화번호", example = "01012341234")
        val phoneNumber: String,
    )
}
