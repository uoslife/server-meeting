package com.uoslife.core.auth.filter

import com.uoslife.core.auth.exception.UnauthorizedException
import com.uoslife.core.auth.jwt.TokenProvider
import com.uoslife.core.auth.jwt.TokenType
import com.uoslife.core.auth.jwt.TokenType.ACCESS_SECRET
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val tokenProvider: TokenProvider,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        if (!request.servletPath.contains("/core/auth/signup")) {
            setAuthentication(request)
        } else {
            setTempAuthentication(request)
        }
        filterChain.doFilter(request, response)
    }

    private fun setTempAuthentication(request: HttpServletRequest) {
        val token = tokenProvider.resolveToken(request) ?: throw UnauthorizedException()

        if (tokenProvider.validateJwtToken(token, TokenType.REFRESH_SECRET)) {
            val authentication = tokenProvider.getTempAuthentication(token)
            SecurityContextHolder.getContext().authentication = authentication
        }
    }

    private fun setAuthentication(request: HttpServletRequest) {
        val token = tokenProvider.resolveToken(request) ?: throw UnauthorizedException()

        if (StringUtils.hasText(token) && tokenProvider.validateJwtToken(token, ACCESS_SECRET)) {
            val authentication = tokenProvider.getAuthentication(token)
            SecurityContextHolder.getContext().authentication = authentication
        }
    }
}
