package uoslife.servermeeting.global.auth.dto.response

data class UserProfileResponse(
    val id: Long,
    val emailId: String,
    val name: String,
    val phoneNumber: String?,
    val kakaoId: String?,
    val gender: String?,
    val birthDate: String?,
    val department: String?,
)
