package uoslife.servermeeting.verification.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "이메일 인증코드 전송 응답")
data class SendVerificationEmailResponse(
    @Schema(description = "인증코드 만료 시각 (Unix timestamp)", example = "1699919400000")
    val expirationTime: Long,
    @Schema(description = "인증코드 유효 시간 (초)", example = "300") val validDuration: Long
)
