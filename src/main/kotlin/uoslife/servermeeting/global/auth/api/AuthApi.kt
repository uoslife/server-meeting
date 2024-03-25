package uoslife.servermeeting.global.auth.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uoslife.servermeeting.global.auth.dto.response.TokenResponse
import uoslife.servermeeting.global.auth.service.AuthService
import uoslife.servermeeting.global.error.ErrorResponse

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Auth API")
class AuthApi(
    private val authService: AuthService,
) {
    @Operation(summary = "토큰 갱신", description = "refresh 토큰으로 access 토큰 갱신")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "토큰 반환",
                    content = [Content(schema = Schema(implementation = TokenResponse::class))]
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
                                                "{message: Token is not valid., status: 400, code: T01}"
                                        )]
                            )]
                ),
            ]
    )
    @PostMapping("/refresh")
    fun refreshToken(request: HttpServletRequest): ResponseEntity<TokenResponse> {
        val tokenResponse: TokenResponse = authService.refreshAccessToken(request)

        return ResponseEntity.ok().body(tokenResponse)
    }

    @Operation(summary = "시대생 토큰으로 회원가입(or 로그인)")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "반환값 없음",
                    content = [Content(schema = Schema(implementation = Unit::class))]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "이미 회원가입 되어있음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: User is already Existing., status: 400, code: U07}"
                                        )]
                            )]
                ),
            ]
    )
    @PostMapping("/uos/signUp")
    fun signUpUOS(request: HttpServletRequest): ResponseEntity<TokenResponse> {
        val bearerToken: String = request.getHeader("Authorization")
        val tokenResponse: TokenResponse = authService.signUpFromUoslife(bearerToken)

        return ResponseEntity.ok().body(tokenResponse)
    }

    @Operation(summary = "시립대 학생 로그인", description = "시립대 학생들은 시대생 앱의 토큰을 통해 로그인")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "토큰 반환",
                    content = [Content(schema = Schema(implementation = TokenResponse::class))]
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
                    responseCode = "400",
                    description = "로그인 실패",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Login failed., status: 400, code: L01}"
                                        )]
                            )]
                ),
            ]
    )
    @PostMapping("/uos/signIn")
    fun signIn(request: HttpServletRequest): ResponseEntity<TokenResponse> {
        val bearerToken: String = request.getHeader("Authorization")
        val tokenResponse: TokenResponse = authService.signIn(bearerToken)

        return ResponseEntity.ok().body(tokenResponse)
    }
}
