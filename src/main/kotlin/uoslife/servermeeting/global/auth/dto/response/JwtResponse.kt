package uoslife.servermeeting.global.auth.dto.response

import uoslife.servermeeting.global.auth.security.SecurityConstants

data class JwtResponse(
    private val rawAccessToken: String
) {
    val accessToken: String
        get() = SecurityConstants.TOKEN_PREFIX + rawAccessToken
}
