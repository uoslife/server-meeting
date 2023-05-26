package uoslife.servermeeting.domain.user.application.response

data class NicknameCheckResponse (
    val message: String,
    val duplication: Boolean
)


