package uoslife.servermeeting.global.auth.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.http.HttpServletRequest
import java.util.Date
import javax.crypto.SecretKey
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import uoslife.servermeeting.global.auth.dto.response.TokenResponse
import uoslife.servermeeting.global.auth.exception.InvalidTokenException
import uoslife.servermeeting.global.auth.security.JwtUserDetailsService
import uoslife.servermeeting.global.auth.service.AccountService
import uoslife.servermeeting.user.entity.User

@Component
class TokenProvider(
    private val jwtUserDetailsService: JwtUserDetailsService,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(TokenProvider::class.java)
        private val authorizationHeader: String = "Authorization"
        private val bearerPrefix: String = "Bearer "
    }

    fun getAuthentication(accessToken: String): UsernamePasswordAuthenticationToken {
        val principal: JwtUserDetails = jwtUserDetailsService.loadUserByUsername(accessToken)
        return UsernamePasswordAuthenticationToken(principal, "", principal.authorities)
    }

    fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(authorizationHeader)

        return bearerToken
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(bearerPrefix)) {
//            return bearerToken.substring(7)
//        }
//        return null
    }

    fun trimRefreshToken(refreshToken: String): String {
        return refreshToken.substring(13)
    }
}
