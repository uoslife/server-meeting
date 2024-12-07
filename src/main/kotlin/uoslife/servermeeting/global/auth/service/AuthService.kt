package uoslife.servermeeting.global.auth.service

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import uoslife.servermeeting.global.auth.dto.response.JwtResponse
import uoslife.servermeeting.global.auth.exception.*
import uoslife.servermeeting.global.auth.security.JwtTokenProvider
import uoslife.servermeeting.global.auth.security.SecurityConstants
import uoslife.servermeeting.global.auth.util.CookieUtils
import uoslife.servermeeting.global.util.RequestUtils

@Service
class AuthService(
    private val jwtTokenProvider: JwtTokenProvider,
    private val cookieUtils: CookieUtils,
    private val requestUtils: RequestUtils,
    @Value("\${jwt.refresh.expiration}") private val refreshTokenExpiration: Long,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(AuthService::class.java)
    }

    fun getAuthenticatedUserId(token: String): Long {
        val jwt = extractToken(token)

        if (!jwtTokenProvider.validateAccessToken(jwt)) {
            throw JwtTokenInvalidSignatureException()
        }
        return jwtTokenProvider.getUserIdFromAccessToken(jwt)
    }

    fun issueTokens(userId: Long, response: HttpServletResponse): JwtResponse {
        val accessToken = jwtTokenProvider.createAccessToken(userId)
        val refreshToken = jwtTokenProvider.createRefreshToken(userId)

        jwtTokenProvider.saveRefreshToken(userId, refreshToken)
        cookieUtils.addRefreshTokenCookie(response, refreshToken, refreshTokenExpiration)

        return JwtResponse(accessToken)
    }

    fun reissueTokens(request: HttpServletRequest, response: HttpServletResponse): JwtResponse {
        val requestInfo = requestUtils.toRequestInfoDto(request)
        val refreshToken =
            cookieUtils.getRefreshTokenFromCookie(request)
                ?: run {
                    logger.warn("[재발급 실패(토큰없음)] $requestInfo")
                    throw JwtRefreshTokenNotFoundException()
                }
        try {
            val userId = jwtTokenProvider.getUserIdFromRefreshToken(refreshToken)

            val storedToken = jwtTokenProvider.getStoredRefreshToken(userId)
            if (storedToken != refreshToken) {
                logger.warn("[재발급 실패(재사용)] $requestInfo")
                throw JwtRefreshTokenReusedException()
            }

            val newAccessToken = jwtTokenProvider.createAccessToken(userId)
            val newRefreshToken = jwtTokenProvider.createRefreshToken(userId)

            jwtTokenProvider.saveRefreshToken(userId, newRefreshToken)

            cookieUtils.addRefreshTokenCookie(response, newRefreshToken, refreshTokenExpiration)
            logger.info("[재발급 성공] userId: $userId")
            return JwtResponse(newAccessToken)
        } catch (e: ExpiredJwtException) {
            logger.warn("[재발급 실패(만료)] $requestInfo")
            throw JwtRefreshTokenExpiredException()
        } catch (e: JwtException) {
            logger.warn("[재발급 실패(서명)] $requestInfo")
            throw JwtTokenInvalidSignatureException()
        }
    }

    fun logout(request: HttpServletRequest, response: HttpServletResponse) {
        val requestInfo = requestUtils.toRequestInfoDto(request)

        val refreshToken = cookieUtils.getRefreshTokenFromCookie(request)
        if (refreshToken != null) {
            try {
                val userId = jwtTokenProvider.getUserIdFromRefreshToken(refreshToken)
                jwtTokenProvider.deleteRefreshToken(userId)
            } catch (e: JwtException) {
                logger.warn("[로그아웃 요청] 유효하지 않은 리프레시 토큰 사용 $requestInfo")
            }
        } else {
            logger.warn("[로그아웃 요청] 리프레시 토큰 없음 $requestInfo")
        }
        cookieUtils.deleteRefreshTokenCookie(response)
    }

    private fun extractToken(token: String): String {
        if (!token.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            throw JwtTokenInvalidFormatException()
        }
        return token.substring(SecurityConstants.TOKEN_PREFIX.length)
    }
}
