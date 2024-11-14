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
import uoslife.servermeeting.user.service.UserService

@RestController
@RequestMapping("/api/auth")
class AuthApi(
    private val authService: AuthService,
    private val emailVerificationService: EmailVerificationService,
    private val userService: UserService,
) {
    @PostMapping("/reissue")
    fun reissue(
        @CookieValue("refresh_token") refreshToken: String,
        response: HttpServletResponse
    ): ResponseEntity<JwtResponse> {
        val accessToken = authService.reissueAccessToken(refreshToken)
        return ResponseEntity.ok(accessToken)
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
        response: HttpServletResponse
    ): ResponseEntity<JwtResponse> {
        emailVerificationService.verifyCode(email, code)
        val userId = userService.createUserByEmail(email)
        val accessToken = authService.issueTokens(userId, response)
        return ResponseEntity.ok(accessToken)
    }
}
