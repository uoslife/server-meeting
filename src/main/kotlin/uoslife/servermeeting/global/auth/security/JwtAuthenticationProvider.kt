package uoslife.servermeeting.global.auth.jwt

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import uoslife.servermeeting.global.auth.security.JwtUserDetailsService

@Component
class JwtAuthenticationProvider(private val userDetailsService: JwtUserDetailsService) :
    AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication {
        val id = authentication.principal as Long
        val userDetails: JwtUserDetails = userDetailsService.loadUserByUsername(id.toString())
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return JwtAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}
