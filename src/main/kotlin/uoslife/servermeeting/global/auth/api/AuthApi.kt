package uoslife.servermeeting.global.auth.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.*
import uoslife.servermeeting.global.auth.dto.response.SendVerificationEmailResponse
import uoslife.servermeeting.global.auth.service.EmailVerificationService
import uoslife.servermeeting.global.auth.dto.response.JwtResponse
import uoslife.servermeeting.global.auth.service.AuthService

@RestController
@RequestMapping("/api/auth")
class AuthApi(
    private val authService: AuthService,
    private val emailVerificationService: EmailVerificationService
) {
    @PostMapping("/reissue")
    fun reissue(
        @CookieValue("refresh_token") refreshToken: String,
        response: HttpServletResponse
    ): ResponseEntity<JwtResponse> {
        return ResponseEntity.ok(authService.reissueAccessToken(refreshToken))
    }

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
        // TODO: 유저 생성
        // TODO: 토큰 반환
    }
}
