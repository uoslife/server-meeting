package uoslife.servermeeting.admin.dto.request

import io.swagger.v3.oas.annotations.media.Schema

data class ResetEmailRequest(
    @Schema(description = "이메일", example = "example@uos.ac.kr") val email: String,
)
