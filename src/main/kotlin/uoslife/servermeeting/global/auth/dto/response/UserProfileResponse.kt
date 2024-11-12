package uoslife.servermeeting.global.auth.dto.response

data class UserProfileResponse(
    val email: String?,
    val name: String?,
    val phoneNumber: String?,
    val kakaoTalkId: String?,
    val gender: String?,
)
