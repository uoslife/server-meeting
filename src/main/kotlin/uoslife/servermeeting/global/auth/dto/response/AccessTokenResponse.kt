package uoslife.servermeeting.global.auth.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class AccessTokenResponse(
    @Schema(description = "access token") val accessToken: String,
)
