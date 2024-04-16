package uoslife.servermeeting.global.auth.dto.response

import io.swagger.v3.oas.annotations.media.Schema

/** Access Token과 Refresh Token을 가지는 DTO 내부 로직에만 사용 */
data class TokenResponse(
    @Schema(description = "액세스 토큰") val accessToken: String,
    @Schema(description = "리프레쉬 토큰") val refreshToken: String,
)
