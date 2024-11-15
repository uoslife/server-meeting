package uoslife.servermeeting.verification.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size


@Schema(description = "이메일 인증코드 검증용 요청")
data class VerifyEmailRequest(
    @field:Email(message = "올바른 이메일 형식이 아닙니다")
    @field:NotBlank(message = "이메일은 필수입니다")
    val email: String,

    @field:NotBlank(message = "인증 코드는 필수입니다")
    @field:Size(min = 4, max = 4, message = "인증 코드는 4자리여야 합니다")
    val code: String
)
