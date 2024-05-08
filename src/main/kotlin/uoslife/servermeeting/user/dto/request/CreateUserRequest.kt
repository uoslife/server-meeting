package uoslife.servermeeting.user.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

class CreateUserRequest(
    @Schema(description = "계정 서비스 조회를 유저 ID", nullable = false) @field:NotNull val userId: Long,
)
