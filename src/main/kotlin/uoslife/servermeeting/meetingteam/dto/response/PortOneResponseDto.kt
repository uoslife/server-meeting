package uoslife.servermeeting.meetingteam.dto.response

class PortOneResponseDto {
    data class PortOneRequestPaymentResponse(
        var merchantUid: String,
        var price: Int,
        var phoneNumber: String,
    )
}
