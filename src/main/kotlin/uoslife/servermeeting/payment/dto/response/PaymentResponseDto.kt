package uoslife.servermeeting.payment.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.payment.entity.enums.PaymentStatus

class PaymentResponseDto {
    companion object {
        private const val PAYMENT_SUCCESS = "paid"
    }
    data class PaymentRequestResponse(
        @Schema(description = "결제 상품 고유 번호") @field:NotBlank var merchantUid: String,
        @Schema(description = "가격", example = "3000") @field:NotNull var price: Int,
        @Schema(description = "전화번호", example = "01012341234")
        @field:NotBlank
        var phoneNumber: String,
        @Schema(description = "이름", example = "나인규") @field:NotBlank var name: String,
        @Schema(description = "주문명", example = "SINGLE") @field:NotNull var productName: TeamType,
        @Schema(description = "결제 상태", example = "SUCCESS") val paymentStatus: PaymentStatus,
    )

    data class PaymentCheckResponse(
        @Schema(description = "결제 성공 여부", example = "true")
        @field:NotNull
        var paymentSuccess: Boolean,
        @Schema(description = "결제 실패 시 메세지", example = "") var message: String,
    )

    data class PaymentRefundResponse(
        @Schema(description = "환불 성공 여부", example = "true")
        @field:NotNull
        var cancelSuccess: Boolean,
        @Schema(description = "환불 실패 시 메세지", example = "") var message: String,
    )

    data class NotMatchedPaymentRefundResponse(val refundFailedList: MutableList<String>?)

    data class PaymentWebhookResponse(
        @Schema(description = "IMP_UID", example = "c49c5e35-12cd-44e0-b45a-ba930543a13e")
        val imp_uid: String?,
        @Schema(description = "Merchant_UID", example = "c49c5e35-12cd-44e0-b45a-ba930543a13e")
        val merchant_uid: String?,
        @Schema(description = "Portone 결제 여부", example = PAYMENT_SUCCESS) var status: String?,
    ) {
        fun isSuccess(): Boolean {
            return status == PAYMENT_SUCCESS
        }
    }
}
