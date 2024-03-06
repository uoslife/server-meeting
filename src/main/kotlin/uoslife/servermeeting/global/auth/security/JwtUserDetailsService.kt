package com.uoslife.core.auth.security

import uoslife.servermeeting.user.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class JwtUserDetailsService(private val userService: UserService) : UserDetailsService {

    companion object {
        private val logger = LoggerFactory.getLogger(JwtUserDetailsService::class.java)
    }
    override fun loadUserByUsername(userId: String): JwtUserDetails {
        return JwtUserDetails(
            id = userId,
            authorities = MutableList<GrantedAuthority>(1) { GrantedAuthority { "ROLE_USER" } },
        )
    }
}
