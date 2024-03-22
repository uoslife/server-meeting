package uoslife.servermeeting.verification.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

data class VerificationCodeSendResponse(
    @field:NotNull @Schema(description = "전송 결과", example = "true") val result: Boolean
)
