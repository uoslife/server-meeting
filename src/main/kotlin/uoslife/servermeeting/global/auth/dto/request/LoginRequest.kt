package uoslife.servermeeting.global.auth.dto.request

import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @NotBlank val email: String,
    @NotBlank val deviceSecret: String,
)
