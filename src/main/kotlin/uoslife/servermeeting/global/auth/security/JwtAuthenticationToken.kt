package com.uoslife.core.auth.security

import org.springframework.security.authentication.AbstractAuthenticationToken

class JwtAuthenticationToken(
    private val id: Long,
) : AbstractAuthenticationToken(null) {

    override fun getPrincipal(): Long {
        return id
    }

    override fun getCredentials(): String {
        return ""
    }
}
