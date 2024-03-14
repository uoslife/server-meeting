package uoslife.servermeeting.global.auth.api

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uoslife.servermeeting.global.auth.dto.request.LoginRequest
import uoslife.servermeeting.global.auth.dto.request.MigrationRequest
import uoslife.servermeeting.global.auth.dto.response.TokenResponse
import uoslife.servermeeting.global.auth.service.AuthService

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Auth API")
class AuthApi(
    private val authService: AuthService,
) {
    @PostMapping("/refresh")
    fun refreshToken(request: HttpServletRequest): ResponseEntity<TokenResponse> {
        val tokenResponse: TokenResponse = authService.refreshAccessToken(request)

        return ResponseEntity.ok().body(tokenResponse)
    }

    @PostMapping("/uos/migrate")
    fun migrateUOS(@RequestBody @Valid migrationRequest: MigrationRequest): ResponseEntity<Unit> {
        authService.migrateFromUoslife(migrationRequest)

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @PostMapping("/uos/login")
    fun login(@RequestBody @Valid loginRequest: LoginRequest): ResponseEntity<TokenResponse> {
        val tokenResponse: TokenResponse = authService.login(loginRequest)

        return ResponseEntity.ok().body(tokenResponse)
    }
}
