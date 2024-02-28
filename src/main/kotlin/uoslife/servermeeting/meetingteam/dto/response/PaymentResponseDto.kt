package uoslife.servermeeting.meetingteam.dto.response

class PaymentResponseDto {
    data class PaymentRequestResponse(
        var merchantUid: String,
        var price: Int,
        var phoneNumber: String,
    )

    data class PaymentCheckResponse(
        var paymentSuccess: Boolean,
        var message: String,
    )

    data class PaymentRefundResponse(
        var cancelSuccess: Boolean,
        var message: String,
    )
}
