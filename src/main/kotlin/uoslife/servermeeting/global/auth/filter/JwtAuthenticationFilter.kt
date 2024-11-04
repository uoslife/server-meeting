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
import uoslife.servermeeting.global.auth.jwt.JwtUserDetails
import uoslife.servermeeting.global.auth.service.AuthService
import uoslife.servermeeting.global.auth.service.UOSLIFEAccountService

@Component
class JwtAuthenticationFilter(
    private val authService: AuthService,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        //        setAuthentication(request, response)
        val token = request.getHeader("Authorization") ?: throw UnauthorizedException()

        try {
            val profile = authService.authenticateToken(token)

            val principal =
                JwtUserDetails(
                    id = profile.id,
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
