package uoslife.servermeeting.global.auth.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class AccessTokenResponse(
    @Schema(description = "액세스 토큰") val accessToken: String,
)
