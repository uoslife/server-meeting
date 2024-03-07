package uoslife.servermeeting.user.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class CheckUserRequest(
    @NotBlank @Schema(description = "이메일 주소", example = "gustmd5715@uos.ac.kr") val email: String
)
