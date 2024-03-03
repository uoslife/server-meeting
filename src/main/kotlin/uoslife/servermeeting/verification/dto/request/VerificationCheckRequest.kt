package uoslife.servermeeting.verification.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class VerificationCheckRequest(
    @NotBlank
    @Schema(description = "이메일 주소", example = "gustmd5715@uos.ac.kr")
    val email: String,
    @NotBlank
    @Schema(description = "인증코드", example = "123456")
    val code: String)
