package uoslife.servermeeting.global.auth.dto.request

import jakarta.validation.constraints.NotBlank

data class MigrationRequest (
    @NotBlank val name: String,
    @NotBlank val phoneNumber: String,
    @NotBlank val email: String,
)
