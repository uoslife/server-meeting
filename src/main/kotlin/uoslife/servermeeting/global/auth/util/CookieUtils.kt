package uoslife.servermeeting.global.auth.util

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class CookieUtils(
    @Value("\${app.cookie.domain}") private val domain: String,
    @Value("\${app.cookie.secure}") private val secure: Boolean
) {
    fun addRefreshTokenCookie(response: HttpServletResponse, refreshToken: String, maxAge: Long) {
        val cookie = Cookie("refresh_token", refreshToken).apply {
            this.domain = this@CookieUtils.domain
            this.isHttpOnly = true
            this.secure = this@CookieUtils.secure
            this.path = "/"
            this.maxAge = maxAge.toInt()
        }
        response.addCookie(cookie)
    }

    fun deleteRefreshTokenCookie(response: HttpServletResponse) {
        val cookie = Cookie("refresh_token", null).apply {
            this.domain = this@CookieUtils.domain
            this.isHttpOnly = true
            this.secure = this@CookieUtils.secure
            this.path = "/"
            this.maxAge = 0
        }
        response.addCookie(cookie)
    }
}
