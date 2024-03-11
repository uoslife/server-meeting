package uoslife.servermeeting.global.auth.dto.response

data class TokenResponse(
    val accessToken: String?,
    val refreshToken: String?,
)
