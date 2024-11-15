package uoslife.servermeeting.global.auth.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import uoslife.servermeeting.global.auth.exception.UnauthorizedException
import uoslife.servermeeting.global.auth.security.JwtUserDetails
import uoslife.servermeeting.global.auth.security.SecurityConstants
import uoslife.servermeeting.global.auth.service.AuthService

@Component
class JwtAuthenticationFilter(
    private val authService: AuthService,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val token =
            request.getHeader(SecurityConstants.TOKEN_HEADER)
                ?: return filterChain.doFilter(request, response)

        try {
            val userId = authService.getAuthenticatedUserId(token)

            val principal =
                JwtUserDetails(
                    id = userId.toString(),
                    authorities = MutableList(1) { GrantedAuthority { "ROLE_USER" } },
                )

            SecurityContextHolder.getContext().authentication =
                UsernamePasswordAuthenticationToken(principal, "", principal.authorities)

            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            logger.error(e)
            throw UnauthorizedException()
        }
    }
}
