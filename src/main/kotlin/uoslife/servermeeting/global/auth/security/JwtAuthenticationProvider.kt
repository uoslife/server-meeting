package uoslife.servermeeting.global.auth.jwt

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import uoslife.servermeeting.global.auth.security.JwtUserDetailsService

@Component
class JwtAuthenticationProvider(private val userDetailsService: JwtUserDetailsService) :
    AuthenticationProvider {
    // TODO: 번호인증시 인증토큰을 발급하는 로직으로 변경 + USER_ID로 발급은 다른 함수로 분리
    override fun authenticate(authentication: Authentication): Authentication {
        val id = authentication.principal as Long
        val userDetails: JwtUserDetails = userDetailsService.loadUserByUsername(id.toString())
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return JwtAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}
