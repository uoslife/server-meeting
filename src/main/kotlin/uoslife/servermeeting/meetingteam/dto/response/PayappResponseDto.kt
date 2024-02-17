package uoslife.servermeeting.meetingteam.dto.response

class PayappResponseDto {
    data class PayappRequestStatusResponse(
        val state: Int,
        val errorMessage: String,
        val mulNo: Int,
        val payUrl: String,
        val qrUrl: String
    )
}
