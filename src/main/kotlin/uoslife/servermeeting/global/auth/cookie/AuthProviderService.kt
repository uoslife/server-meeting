package uoslife.servermeeting.global.auth.cookie

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service

@Service
class AuthProviderService() {

    fun getCookieFromRequest(request: HttpServletRequest): String? {
        return null
    }

    fun validateCookie(jwtToken: String): Boolean {
        return false
    }

    fun getAuthentication(token: String): Authentication? {
        val authorities: MutableList<GrantedAuthority> = ArrayList()
        authorities.add(SimpleGrantedAuthority("ROLE_USER"))

        val userDetails = User.builder()
            .username("")
            .password("")
            .authorities(authorities)
            .build()

        return UsernamePasswordAuthenticationToken(userDetails, "", authorities)
    }
}
