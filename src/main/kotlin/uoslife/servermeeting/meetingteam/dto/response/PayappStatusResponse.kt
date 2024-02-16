package uoslife.servermeeting.meetingteam.dto.response

data class PayappStatusResponse(
    val state: Int,
    val errorMessage: String,
    val mulNo: Int,
    val payUrl: String,
    val qrUrl: String
)
