package uoslife.servermeeting.verification.dto.request

import jakarta.validation.constraints.NotBlank

data class VerificationCheckRequest(@NotBlank val email: String, @NotBlank val code: String)
