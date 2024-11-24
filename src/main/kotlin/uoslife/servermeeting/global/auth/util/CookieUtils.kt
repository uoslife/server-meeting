package uoslife.servermeeting.global.auth.util

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component

@Component
class CookieUtils(
    @Value("\${app.cookie.domain}") private val domain: String,
    @Value("\${app.cookie.secure}") private val secure: Boolean
) {
    fun addRefreshTokenCookie(response: HttpServletResponse, refreshToken: String, maxAge: Long) {
        val encodedRefreshToken = URLEncoder.encode(refreshToken, StandardCharsets.UTF_8)
        val cookie =
            ResponseCookie.from("refresh_token", encodedRefreshToken)
                .domain(domain)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(maxAge)
                .build()
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
    }

    fun deleteRefreshTokenCookie(response: HttpServletResponse) {
        val cookie =
            ResponseCookie.from("refresh_token", "")
                .domain(domain)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(0)
                .build()
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
    }

    fun getRefreshTokenFromCookie(request: HttpServletRequest): String? {
        return request.cookies
            ?.find { it.name == "refresh_token" }
            ?.let { cookie -> URLDecoder.decode(cookie.value, StandardCharsets.UTF_8) }
    }
}
