package uoslife.servermeeting.global.auth.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import uoslife.servermeeting.global.auth.security.SecurityConstants

@Schema(description = "JWT 응답")
data class JwtResponse(
    @Schema(description = "JWT 접두사가 포함되지 않은 원본 액세스 토큰") private val rawAccessToken: String
) {
    val accessToken: String
        get() = SecurityConstants.TOKEN_PREFIX + rawAccessToken
}
