package uoslife.servermeeting.global.auth.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import uoslife.servermeeting.global.auth.jwt.JwtUserDetails

@Service
class JwtUserDetailsService : UserDetailsService {

    override fun loadUserByUsername(userId: String): JwtUserDetails {
        return JwtUserDetails(
            id = userId,
            authorities = MutableList<GrantedAuthority>(1) { GrantedAuthority { "ROLE_USER" } },
        )
    }
}
