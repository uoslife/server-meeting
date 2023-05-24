package uoslife.servermeeting.domain.user.application.response

data class NicknameCheckResponseDto (
    val message: String,
    val duplication: Boolean
)


