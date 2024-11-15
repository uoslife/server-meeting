package uoslife.servermeeting.global.auth.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "JWT 응답") data class JwtResponse(val accessToken: String)
