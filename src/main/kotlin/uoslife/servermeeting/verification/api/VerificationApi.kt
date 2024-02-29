package uoslife.servermeeting.verification.api

import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uoslife.servermeeting.verification.dto.request.VerificationCheckRequest
import uoslife.servermeeting.verification.dto.request.VerificationRequest
import uoslife.servermeeting.verification.dto.response.*
import uoslife.servermeeting.verification.service.VerificationService

@RestController
@RequestMapping("/api/verification")
class VerificationApi(private val verificationService: VerificationService) {
    @Operation(summary = "메일 인증 코드 전송", description = "메일 인증을 위해 인증 코드를 내포한 메일을 대학 메일로 보냅니다.")
    @PostMapping("/send")
    fun sendMail(
        @RequestBody @Valid verificationRequest: VerificationRequest
    ): ResponseEntity<SendMailResponse> {
        val isSended: Boolean = verificationService.sendMail(verificationRequest)

        return ResponseEntity.ok().body(SendMailResponse(isSended))
    }

    @Operation(summary = "인증 코드 확인", description = "verification 테이블의 인증 코드와 사용자가 입력한 인증 코드를 비교합니다.")
    @PostMapping("/check")
    fun verifyCode(
        @RequestBody @Valid verificationCheckRequest: VerificationCheckRequest
    ): ResponseEntity<VerificationCodeResponse> {
        val isVerified: Boolean =
            verificationService.checkVerificationCode(verificationCheckRequest)

        return ResponseEntity.ok().body(VerificationCodeResponse(isVerified))
    }

    @Operation(summary = "인증 여부 확인", description = "이메일을 조회하여 이미 인증 되었는지 확인한다.")
    @GetMapping("/{email}")
    fun isVerifiedCert(
        @PathVariable @Valid email: String
    ): ResponseEntity<VerificationCheckResponse> {
        val status: Boolean = verificationService.findByEmailAndIsVerified(email)

        return ResponseEntity.ok().body(VerificationCheckResponse(status))
    }
}
