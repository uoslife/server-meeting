package uoslife.servermeeting.global.auth.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class TokenResponse(
    @Schema(description = "access token") val accessToken: String?,
    @Schema(description = "refresh token") val refreshToken: String?,
)
