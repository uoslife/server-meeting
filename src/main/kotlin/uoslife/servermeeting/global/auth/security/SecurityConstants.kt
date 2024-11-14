package uoslife.servermeeting.global.auth.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
object SecurityConstants {

    const val TOKEN_HEADER = "Authorization"
    const val TOKEN_PREFIX = "Bearer "
    const val TOKEN_ISSUER = "UOSLIFE"
    const val TOKEN_AUDIENCE = "UOSLIFE USER"

    @Value("\${jwt.access.expiration}") var ACCESS_TOKEN_EXPIRATION: Long = 0

    @Value("\${jwt.refresh.expiration}") var REFRESH_TOKEN_EXPIRATION: Long = 0
}
