package uoslife.servermeeting.global.auth.security

import org.slf4j.LoggerFactory
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import uoslife.servermeeting.global.auth.dto.response.AccountResponse
import uoslife.servermeeting.global.auth.jwt.JwtUserDetails
import uoslife.servermeeting.global.auth.service.AccountService

@Service
class JwtUserDetailsService(
    private val accountService: AccountService,
) : UserDetailsService {

    companion object {
        private val logger = LoggerFactory.getLogger(JwtUserDetailsService::class.java)
    }
    override fun loadUserByUsername(accessToken: String): JwtUserDetails {
        val profile: AccountResponse = accountService.getMyProfile(accessToken)

        return JwtUserDetails(
            id = profile.id.toString(),
            authorities = MutableList<GrantedAuthority>(1) { GrantedAuthority { "ROLE_USER" } },
        )
    }
}
