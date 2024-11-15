package uoslife.servermeeting.global.auth.security

import org.springframework.stereotype.Component

@Component
object SecurityConstants {
    const val TOKEN_HEADER = "Authorization"
    const val TOKEN_PREFIX = "Bearer "
    const val TOKEN_ISSUER = "UOSLIFE"
    const val TOKEN_AUDIENCE = "UOSLIFE USER"
}
