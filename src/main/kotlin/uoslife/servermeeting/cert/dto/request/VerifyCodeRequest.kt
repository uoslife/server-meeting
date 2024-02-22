package uoslife.servermeeting.cert.dto.request

import jakarta.validation.constraints.NotBlank
import uoslife.servermeeting.cert.dto.University

data class VerifyCodeRequest(
    @NotBlank val university: University,
    @NotBlank val email: String,
    @NotBlank val code: String
)
