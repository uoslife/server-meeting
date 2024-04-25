package uoslife.servermeeting.global.auth.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uoslife.servermeeting.global.auth.dto.response.AccessTokenResponse
import uoslife.servermeeting.global.auth.dto.response.TokenResponse
import uoslife.servermeeting.global.auth.jwt.TokenProvider
import uoslife.servermeeting.global.auth.service.AuthService
import uoslife.servermeeting.global.error.ErrorResponse
import uoslife.servermeeting.global.util.CookieUtil

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Auth API")
class AuthApi(
    private val authService: AuthService,
    private val tokenProvider: TokenProvider,
    private val cookieUtil: CookieUtil,
) {
    @Operation(summary = "토큰 갱신", description = "refresh 토큰으로 access 토큰 갱신")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "토큰 반환",
                    content =
                        [Content(schema = Schema(implementation = AccessTokenResponse::class))]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "해당 유저 정보 없음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: User is not Found., status: 400, code: U02}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "401",
                    description = "부적절한 토큰 정보",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Token is not valid., status: 401, code: T01}"
                                        )]
                            )]
                ),
            ]
    )
    @PostMapping("/refresh")
    fun refreshToken(
        @RequestHeader(value = "Cookie") refreshToken: String,
        response: HttpServletResponse
    ): ResponseEntity<AccessTokenResponse> {
        val trimmedRefreshToken: String = tokenProvider.trimRefreshToken(refreshToken)
        val tokenResponse: TokenResponse = authService.refreshAccessToken(trimmedRefreshToken)
        cookieUtil.setCookieWithRefreshToken(response, tokenResponse.refreshToken)

        return ResponseEntity.ok()
            .body(AccessTokenResponse(accessToken = tokenResponse.accessToken))
    }

    @Operation(summary = "시대생 유저 회원가입 or 로그인", description = "시대생 토큰으로 회원가입(이미 되어 있으면 로그인)")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "토큰 반환",
                    content =
                        [Content(schema = Schema(implementation = AccessTokenResponse::class))]
                ),
                ApiResponse(
                    responseCode = "401",
                    description = "부적절한 토큰 정보",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Token is not valid., status: 401, code: T01}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "500",
                    description = "외부 API 통신 에러 (UOSLIFE)",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: External API Request is failed., status: 500, code: E01}"
                                        )]
                            )]
                )]
    )
    @PostMapping("/uos/signUpOrIn")
    fun signUpOrInUOS(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<AccessTokenResponse> {
        val bearerToken: String = request.getHeader("Authorization")
        val tokenResponse: TokenResponse = authService.signUpOrInFromUoslife(bearerToken)

        cookieUtil.setCookieWithRefreshToken(response, tokenResponse.refreshToken)

        return ResponseEntity.ok()
            .body(AccessTokenResponse(accessToken = tokenResponse.accessToken))
    }
}
