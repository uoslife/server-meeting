package uoslife.servermeeting.cert.dto.request

import uoslife.servermeeting.cert.dto.University

data class CertifyRequest(
    val university: University,
    val email: String,
)
