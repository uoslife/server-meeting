package uoslife.servermeeting.global.auth.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import uoslife.servermeeting.global.auth.exception.UnauthorizedException
import uoslife.servermeeting.global.auth.jwt.TokenProvider
import uoslife.servermeeting.global.auth.jwt.TokenType

class JwtAuthenticationFilter(
    private val tokenProvider: TokenProvider,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        setAuthentication(request)
        filterChain.doFilter(request, response)
    }

    private fun setAuthentication(request: HttpServletRequest) {
        val token = tokenProvider.resolveToken(request) ?: throw UnauthorizedException()

        if (
            StringUtils.hasText(token) &&
                tokenProvider.validateJwtToken(token, TokenType.ACCESS_SECRET)
        ) {
            val authentication = tokenProvider.getAuthentication(token)
            SecurityContextHolder.getContext().authentication = authentication
        }
    }
}
