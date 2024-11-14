package uoslife.servermeeting.global.auth.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
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
import uoslife.servermeeting.global.error.ErrorResponse
import uoslife.servermeeting.global.error.exception.JwtAuthenticationException
import uoslife.servermeeting.user.service.UserService

@Tag(name = "Auth", description = "Auth API")
@RestController
@RequestMapping("/api/auth")
class AuthApi(
    private val authService: AuthService,
    private val emailVerificationService: EmailVerificationService,
    private val userService: UserService,
    private val cookieUtils: CookieUtils,
) {
    @Operation(summary = "토큰 재발급", description = "Refresh Token으로 새로운 Access Token을 발급합니다.")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "토큰 재발급 성공",
                    content = [Content(schema = Schema(implementation = JwtResponse::class))]
                ),
                ApiResponse(
                    responseCode = "401",
                    description = "토큰 재발급 실패",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                            )]
                )]
    )
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

    @Operation(summary = "인증 메일 전송", description = "서울시립대학교 이메일로 인증코드를 전송합니다.")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "이메일 전송 성공",
                    content =
                        [
                            Content(
                                schema =
                                    Schema(implementation = SendVerificationEmailResponse::class)
                            )]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "잘못된 이메일 형식",
                    content = [Content(schema = Schema(implementation = ErrorResponse::class))]
                ),
                ApiResponse(
                    responseCode = "500",
                    description = "이메일 전송 실패",
                    content = [Content(schema = Schema(implementation = ErrorResponse::class))]
                )]
    )
    @PostMapping("/send-verification-email")
    fun sendVerificationEmail(
        @RequestParam email: String
    ): ResponseEntity<SendVerificationEmailResponse> {
        val response = emailVerificationService.sendVerificationEmail(email)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "인증코드 검증", description = "이메일로 전송된 인증코드를 검증하고, 성공 시 토큰을 발급합니다.")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "인증 성공 및 토큰 발급",
                    content = [Content(schema = Schema(implementation = JwtResponse::class))]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "잘못된 인증코드",
                    content = [Content(schema = Schema(implementation = ErrorResponse::class))]
                )]
    )
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
