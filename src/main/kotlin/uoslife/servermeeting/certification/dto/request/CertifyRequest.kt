package uoslife.servermeeting.certification.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import uoslife.servermeeting.certification.dto.University

data class CertifyRequest(
    @NotBlank
    val university: University,

    @NotBlank
    @Pattern(regexp = "\\b[A-Za-z0-9._%+-]+@khu\\.ac\\.kr\\b|\\b[A-Za-z0-9._%+-]+@korea\\.ac\\.kr\\b", message = "Invalid email") // 경희대, 외대 메일 검증 정규 표현식
    val email: String,
)
