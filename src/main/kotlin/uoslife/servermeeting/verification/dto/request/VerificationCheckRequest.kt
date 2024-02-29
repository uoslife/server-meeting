package uoslife.servermeeting.verification.dto.request

import jakarta.validation.constraints.NotBlank
import uoslife.servermeeting.verification.dto.University

data class VerificationCheckRequest(
    @NotBlank val email: String,
    @NotBlank val code: String
)
