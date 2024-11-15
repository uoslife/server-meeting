package uoslife.servermeeting.verification.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uoslife.servermeeting.global.auth.dto.response.JwtResponse
import uoslife.servermeeting.global.auth.service.AuthService
import uoslife.servermeeting.global.error.ErrorResponse
import uoslife.servermeeting.user.service.UserService
import uoslife.servermeeting.verification.dto.response.SendVerificationEmailResponse
import uoslife.servermeeting.verification.service.EmailVerificationService

@Tag(name = "Verification", description = "Verification API")
@RestController
@RequestMapping("/api/verification")
class VerificationApi(
    private val emailVerificationService: EmailVerificationService,
    private val userService: UserService,
    private val authService: AuthService,
) {
    @Operation(summary = "인증메일 전송", description = "이메일로 인증코드를 전송합니다.")
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
                    description = "잘못된 요청",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "Invalid Email Format",
                                            value =
                                                "{\"message\": \"Invalid email format.\", \"status\": 400, \"code\": \"E01\"}"
                                        ),
                                        ExampleObject(
                                            name = "Invalid Email Domain",
                                            value =
                                                "{\"message\": \"Email domain is not allowed.\", \"status\": 400, \"code\": \"E02\"}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "429",
                    description = "요청 한도 초과",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{\"message\": \"Daily email send limit exceeded.\", \"status\": 429, \"code\": \"E04\"}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{\"message\": \"Failed to send email.\", \"status\": 500, \"code\": \"E06\"}"
                                        )]
                            )]
                )]
    )
    @PostMapping("/send-email")
    fun sendVerificationEmail(
        @RequestParam email: String
    ): ResponseEntity<SendVerificationEmailResponse> {
        val response = emailVerificationService.sendVerificationEmail(email)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "인증코드 검증", description = "이메일로 전송된 인증코드를 검증하고, 성공 시 유저 생성 및 토큰을 발급합니다.")
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
                    description = "잘못된 요청",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{\"message\": \"Verification code does not match.\", \"status\": 400, \"code\": \"E03\"}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "429",
                    description = "요청 한도 초과",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{\"message\": \"Daily verification attempt limit exceeded.\", \"status\": 429, \"code\": \"E05\"}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "401",
                    description = "인증 오류",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "Token Not Found",
                                            value =
                                                "{\"message\": \"Token not found\", \"status\": 401, \"code\": \"J001\"}"
                                        ),
                                        ExampleObject(
                                            name = "Token Expired",
                                            value =
                                                "{\"message\": \"Token has expired\", \"status\": 401, \"code\": \"J002\"}"
                                        ),
                                        ExampleObject(
                                            name = "Invalid Token Format",
                                            value =
                                                "{\"message\": \"Invalid token format\", \"status\": 401, \"code\": \"J003\"}"
                                        ),
                                        ExampleObject(
                                            name = "Invalid Token Signature",
                                            value =
                                                "{\"message\": \"Invalid token signature\", \"status\": 401, \"code\": \"J004\"}"
                                        ),
                                        ExampleObject(
                                            name = "Refresh Token Not Found",
                                            value =
                                                "{\"message\": \"Refresh token not found\", \"status\": 401, \"code\": \"J005\"}"
                                        ),
                                        ExampleObject(
                                            name = "Refresh Token Expired",
                                            value =
                                                "{\"message\": \"Refresh token has expired\", \"status\": 401, \"code\": \"J006\"}"
                                        )]
                            )]
                )]
    )
    @PostMapping("/verify-code")
    fun verifyCode(
        @RequestParam email: String,
        @RequestParam code: String,
        @Valid @RequestBody request: VerifyEmailRequest,
        response: HttpServletResponse
    ): ResponseEntity<JwtResponse> {
        emailVerificationService.verifyCode(email, code)
        val userId = userService.createUserByEmail(email)
        val accessToken = authService.issueTokens(userId, response)
        return ResponseEntity.ok(accessToken)
    }
}
