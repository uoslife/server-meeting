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
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uoslife.servermeeting.global.auth.dto.response.JwtResponse
import uoslife.servermeeting.global.auth.exception.JwtAuthenticationException
import uoslife.servermeeting.global.auth.service.AuthService
import uoslife.servermeeting.global.auth.util.CookieUtils
import uoslife.servermeeting.global.error.ErrorResponse

@Tag(name = "Auth", description = "Auth API")
@RestController
@RequestMapping("/api/auth")
class AuthApi(
    private val authService: AuthService,
    private val cookieUtils: CookieUtils,
) {
    @Operation(summary = "토큰 재발급", description = "Refresh Token으로 새로운 Access Token을 발급합니다.")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "토큰 재발급 성공",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = JwtResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{\"accessToken\": \"Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "401",
                    description = "토큰 재발급 실패",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "Refresh Token Not Found",
                                            value =
                                                "{\"message\": \"Refresh token not found\", \"status\": 401, \"code\": \"J005\"}"
                                        ),
                                        ExampleObject(
                                            name = "Refresh Token Expired",
                                            value =
                                                "{\"message\": \"Refresh token has expired\", \"status\": 401, \"code\": \"J006\"}"
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
                                        )]
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


    @PostMapping("/logout")
    fun logOut(response: HttpServletResponse): ResponseEntity<Unit> {
        cookieUtils.deleteRefreshTokenCookie(response)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
