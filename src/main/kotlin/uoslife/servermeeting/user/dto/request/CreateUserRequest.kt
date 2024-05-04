package uoslife.servermeeting.user.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

class CreateUserRequest(
    @Schema(description = "계정 서비스 조회를 위한 토큰", nullable = false)
    @field:NotNull
    val accessToken: String
)
