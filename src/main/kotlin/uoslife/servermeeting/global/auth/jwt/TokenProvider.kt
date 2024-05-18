package uoslife.servermeeting.global.auth.jwt

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component
import uoslife.servermeeting.global.auth.security.JwtUserDetailsService

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

    fun resolveToken(request: HttpServletRequest): String {
        val bearerToken = request.getHeader(authorizationHeader)

        return bearerToken
    }

    fun trimRefreshToken(refreshToken: String): String {
        return refreshToken.substring(13)
    }
}
