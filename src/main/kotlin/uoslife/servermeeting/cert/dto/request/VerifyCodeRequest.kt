package uoslife.servermeeting.cert.dto.request

import uoslife.servermeeting.cert.dto.University

data class VerifyCodeRequest(
    val university: University,
    val email: String,
    val code: String
)
