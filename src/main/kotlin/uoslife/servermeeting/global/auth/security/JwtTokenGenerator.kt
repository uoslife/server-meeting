package uoslife.servermeeting.global.auth.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.util.*
import javax.crypto.SecretKey
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class JwtTokenGenerator(
    @Value("\${jwt.access.secret}") private val accessSecret: String,
    @Value("\${jwt.refresh.secret}") private val refreshSecret: String,
    @Value("\${jwt.access.expiration}") private val accessTokenExpiration: Long,
    @Value("\${jwt.refresh.expiration}") private val refreshTokenExpiration: Long,
) {
    private val accessKey = Keys.hmacShaKeyFor(accessSecret.toByteArray())
    private val refreshKey = Keys.hmacShaKeyFor(refreshSecret.toByteArray())

    fun createAccessToken(id: Long): String {
        return createToken(id, accessKey, accessTokenExpiration)
    }

    fun createRefreshToken(id: Long): String {
        return createToken(id, refreshKey, refreshTokenExpiration)
    }

    private fun createToken(id: Long, key: SecretKey, expiration: Long): String {
        val now = Date()
        val validity = Date(now.time + expiration)

        return Jwts.builder()
            .subject(id.toString())
            .issuer(SecurityConstants.TOKEN_ISSUER)
            .audience()
            .add(SecurityConstants.TOKEN_AUDIENCE)
            .and()
            .issuedAt(now)
            .expiration(validity)
            .signWith(key, Jwts.SIG.HS256)
            .compact()
    }
}
