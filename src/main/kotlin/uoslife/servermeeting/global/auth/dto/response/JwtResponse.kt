package uoslife.servermeeting.global.auth.dto.response

data class JwtResponse(
    val accessToken: String,
    val refreshToken: String,
)
