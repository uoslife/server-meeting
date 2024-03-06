package uoslife.servermeeting.verification.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class VerificationSendRequest(
    @NotBlank
    @Pattern(
        regexp = "\\b[A-Za-z0-9._%+-]+@khu\\.ac\\.kr\\b|\\b[A-Za-z0-9._%+-]+@hufs\\.ac\\.kr\\b",
        message = "Invalid Email"
    ) // 경희대, 외대 메일 검증 정규 표현식
    @Schema(description = "이메일 주소", example = "gustmd5715@uos.ac.kr")
    val email: String,
)
