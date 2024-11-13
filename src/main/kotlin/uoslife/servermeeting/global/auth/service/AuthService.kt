package uoslife.servermeeting.global.auth.service

import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service
import uoslife.servermeeting.global.auth.dto.response.JwtResponse
import uoslife.servermeeting.global.auth.exception.UnauthorizedException
import uoslife.servermeeting.global.auth.security.JwtTokenProvider
import uoslife.servermeeting.global.auth.security.SecurityConstants
import uoslife.servermeeting.global.auth.util.CookieUtils

@Service
class AuthService(
    private val jwtTokenProvider: JwtTokenProvider,
    private val cookieUtils: CookieUtils
) {
    fun authenticateToken(token: String): Long {
        if (!token.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            throw UnauthorizedException()
        }

        val jwt = token.substring(SecurityConstants.TOKEN_PREFIX.length)
        if (!jwtTokenProvider.validateAccessToken(jwt)) {
            throw UnauthorizedException()
        }

        return jwtTokenProvider.getUserIdFromAccessToken(jwt)
    }


    fun issueTokens(userId: Long, response: HttpServletResponse): JwtResponse {
        val accessToken = jwtTokenProvider.createAccessToken(userId)
        val refreshToken = jwtTokenProvider.createRefreshToken(userId)

        cookieUtils.addRefreshTokenCookie(
            response,
            SecurityConstants.TOKEN_PREFIX + refreshToken,
            SecurityConstants.REFRESH_TOKEN_EXPIRATION
        )

        return JwtResponse(
            accessToken = SecurityConstants.TOKEN_PREFIX + accessToken
        )
    }

    fun reissue(refreshToken: String, response: HttpServletResponse): JwtResponse {
        if (!refreshToken.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            throw UnauthorizedException()
        }

        val jwt = refreshToken.substring(SecurityConstants.TOKEN_PREFIX.length)
        if (!jwtTokenProvider.validateRefreshToken(jwt)) {
            throw UnauthorizedException()
        }

        val userId = jwtTokenProvider.getUserIdFromRefreshToken(jwt)
        val newRefreshToken = jwtTokenProvider.createRefreshToken(userId)

        cookieUtils.addRefreshTokenCookie(
            response,
            SecurityConstants.TOKEN_PREFIX + newRefreshToken,
            SecurityConstants.REFRESH_TOKEN_EXPIRATION
        )

        return JwtResponse(
            accessToken = SecurityConstants.TOKEN_PREFIX + jwtTokenProvider.createAccessToken(userId)
        )
    }
}
