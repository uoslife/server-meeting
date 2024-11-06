package uoslife.servermeeting.global.auth.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uoslife.servermeeting.global.auth.dto.response.SendVerificationEmailResponse
import uoslife.servermeeting.global.auth.service.EmailVerificationService

@RestController
@RequestMapping("/api/auth")
class AuthApi(
    private val emailVerificationService: EmailVerificationService
) {
    @PostMapping("/send-verification-email")
    fun sendVerificationEmail(@RequestParam email: String): ResponseEntity<SendVerificationEmailResponse> {
        val response = emailVerificationService.sendVerificationEmail(email)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/verify-code")
    fun verifyCode(
        @RequestParam email: String,
        @RequestParam code: String,
    ) {
        emailVerificationService.verifyCode(email, code)
        // TODO: 예외 처리 및 상태코드 설정
        // TODO: 토큰 반환
    }
}
