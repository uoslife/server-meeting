package uoslife.servermeeting.global.auth.dto.response

data class SendVerificationEmailResponse(
    val expirationTime: Long, // 만료 시각 (Unix timestamp)
    val validDuration: Long // 유효 시간 (sec)
)
