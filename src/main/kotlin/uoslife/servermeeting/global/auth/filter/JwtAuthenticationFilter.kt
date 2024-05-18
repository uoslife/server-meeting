package uoslife.servermeeting.global.auth.filter

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import uoslife.servermeeting.global.auth.exception.InvalidTokenException
import uoslife.servermeeting.global.auth.exception.UnauthorizedException
import uoslife.servermeeting.global.auth.jwt.JwtUserDetails
import uoslife.servermeeting.global.auth.jwt.TokenProvider
import uoslife.servermeeting.global.auth.service.AccountService
import uoslife.servermeeting.global.error.ErrorResponse
import uoslife.servermeeting.global.error.exception.ErrorCode

class JwtAuthenticationFilter(
    private val accountService: AccountService,
) : OncePerRequestFilter() {
    private val logger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
//        setAuthentication(request, response)
        val token = request.getHeader("Authorization") ?: throw UnauthorizedException()

        try{
            val profile = accountService.getAuthenticatedUserProfile(token)

            val principal = JwtUserDetails(
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
