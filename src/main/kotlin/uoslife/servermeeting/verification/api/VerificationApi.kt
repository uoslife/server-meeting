package uoslife.servermeeting.verification.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
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
@Tag(name = "Verification", description = "이메일 인증 API")
class VerificationApi(private val verificationService: VerificationService) {
    @Operation(summary = "메일 인증 코드 전송", description = "메일 인증을 위해 인증 코드를 내포한 메일을 대학 메일로 보냅니다.")
    @ApiResponse(responseCode = "200", description = "메인 전송 성공")
    @PostMapping("/send")
    fun sendMail(
        @RequestBody @Valid verificationRequest: VerificationRequest
    ): ResponseEntity<SendMailResponse> {
        return ResponseEntity.ok().body(verificationService.sendMail(verificationRequest))
    }

    @Operation(
        summary = "인증 코드 확인",
        description = "verification 테이블의 인증 코드와 사용자가 입력한 인증 코드를 비교합니다."
    )
    @ApiResponse(responseCode = "200", description = "인증 성공, AccessToken 반환")
    @PostMapping("/verify")
    fun verifyCode(
        @RequestBody @Valid verificationCheckRequest: VerificationCheckRequest
    ): ResponseEntity<VerifyCodeResponse> {
        return ResponseEntity.ok()
            .body(verificationService.verifyVerificationCode(verificationCheckRequest))
    }
}
