package uoslife.servermeeting.cert.dto.request

import jakarta.validation.constraints.NotBlank
import uoslife.servermeeting.cert.dto.University

data class CertifyRequest(
    @NotBlank val university: University,
    @NotBlank val email: String,
)
