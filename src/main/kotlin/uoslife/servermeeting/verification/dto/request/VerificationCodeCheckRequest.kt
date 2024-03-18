package uoslife.servermeeting.verification.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import uoslife.servermeeting.verification.dto.University

data class VerificationCodeCheckRequest(
    @field:NotBlank
    @Schema(description = "이메일 주소", example = "gustmd5715@uos.ac.kr")
    val email: String,
    @field:NotBlank @Schema(description = "인증코드", example = "123456") val code: String,
    @field:NotNull @Schema(description = "대학 구분", example = "UOS") val university: University
)
