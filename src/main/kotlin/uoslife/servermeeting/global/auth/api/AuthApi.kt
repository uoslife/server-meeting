package uoslife.servermeeting.global.auth.api

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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

    @PostMapping("/migrate")
    fun migrateUOS(@RequestBody @Valid migrationRequest: MigrationRequest): ResponseEntity<Unit> {
        return authService.migrateFromUoslife(migrationRequest)
    }
}
