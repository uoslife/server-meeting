package uoslife.servermeeting.user.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern

data class CheckUserRequest(
    @NotBlank
    @Schema(description = "이메일 주소", example = "gustmd5715@uos.ac.kr")
    val email: String
)
