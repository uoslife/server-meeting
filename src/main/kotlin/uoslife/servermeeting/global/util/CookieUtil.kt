package uoslife.servermeeting.global.util

import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component

@Component
class CookieUtil(
    @Value("\${jwt.refresh.expiration}") private val refreshTokenExpiration: Long,
) {
    fun setCookieWithRefreshToken(response: HttpServletResponse, refreshToken: String): Unit {
        val cookie: ResponseCookie =
            ResponseCookie.from("refreshToken", refreshToken)
                .maxAge(refreshTokenExpiration / 1000) // millsecond -> second
                .path("/")
                .secure(true)
                .httpOnly(true)
                .sameSite("None")
                .build()

        response.setHeader("Set-Cookie", cookie.toString())
    }
}
