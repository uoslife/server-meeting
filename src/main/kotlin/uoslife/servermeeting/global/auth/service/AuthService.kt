package uoslife.servermeeting.global.auth.service

import org.springframework.stereotype.Service
import uoslife.servermeeting.global.auth.dto.response.JwtResponse
import uoslife.servermeeting.global.auth.exception.UnauthorizedException
import uoslife.servermeeting.global.auth.security.JwtTokenProvider
import uoslife.servermeeting.global.auth.security.SecurityConstants

@Service
class AuthService(
    private val jwtTokenProvider: JwtTokenProvider,
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

    fun reissue(refreshToken: String): JwtResponse {
        if (!refreshToken.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            throw UnauthorizedException()
        }

        val jwt = refreshToken.substring(SecurityConstants.TOKEN_PREFIX.length)
        if (!jwtTokenProvider.validateRefreshToken(jwt)) {
            throw UnauthorizedException()
        }

        val userId = jwtTokenProvider.getUserIdFromRefreshToken(jwt)
        return JwtResponse(
            accessToken = SecurityConstants.TOKEN_PREFIX + jwtTokenProvider.createAccessToken(userId),
            refreshToken = SecurityConstants.TOKEN_PREFIX + jwtTokenProvider.createRefreshToken(userId)
        )
    }
}
