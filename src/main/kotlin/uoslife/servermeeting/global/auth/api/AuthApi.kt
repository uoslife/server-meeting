package uoslife.servermeeting.global.auth.api

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uoslife.servermeeting.global.auth.dto.response.JwtResponse
import uoslife.servermeeting.global.auth.dto.response.SendVerificationEmailResponse
import uoslife.servermeeting.global.auth.service.AuthService
import uoslife.servermeeting.global.auth.service.EmailVerificationService
import uoslife.servermeeting.global.auth.util.CookieUtils
import uoslife.servermeeting.global.error.exception.JwtAuthenticationException
import uoslife.servermeeting.user.service.UserService

@RestController
@RequestMapping("/api/auth")
class AuthApi(
    private val authService: AuthService,
    private val emailVerificationService: EmailVerificationService,
    private val userService: UserService,
    private val cookieUtils: CookieUtils,
) {
    @PostMapping("/reissue")
    fun reissue(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<JwtResponse> {
        return try {
            val accessToken = authService.reissueAccessToken(request)
            ResponseEntity.ok(accessToken)
        } catch (e: JwtAuthenticationException) {
            cookieUtils.deleteRefreshTokenCookie(response)
            throw e
        }
    }

    @PostMapping("/send-verification-email")
    fun sendVerificationEmail(
        @RequestParam email: String
    ): ResponseEntity<SendVerificationEmailResponse> {
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
