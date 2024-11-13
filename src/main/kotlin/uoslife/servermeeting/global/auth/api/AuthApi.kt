package uoslife.servermeeting.global.auth.api

import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import uoslife.servermeeting.global.auth.dto.response.JwtResponse
import uoslife.servermeeting.global.auth.service.AuthService

@RestController
@RequestMapping("/api/auth")
class AuthApi(
    private val authService: AuthService,
) {
    @PostMapping("/reissue")
    fun reissue(
        @CookieValue("refresh_token") refreshToken: String,
        response: HttpServletResponse
    ): ResponseEntity<JwtResponse> {
        return ResponseEntity.ok(authService.reissueAccessToken(refreshToken))
    }
}
