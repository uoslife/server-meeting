package uoslife.servermeeting.global.auth.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uoslife.servermeeting.global.auth.dto.response.JwtResponse
import uoslife.servermeeting.global.auth.service.AuthService

@RestController
@RequestMapping("/api/auth")
class AuthApi(
    private val authService: AuthService,
) {
    @PostMapping("/reissue")
    fun reissue(@RequestHeader("Authorization") refreshToken: String): ResponseEntity<JwtResponse> {
        return ResponseEntity.ok(authService.reissue(refreshToken))
    }
}
