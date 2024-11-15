package uoslife.servermeeting.global.auth.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.util.*
import javax.crypto.SecretKey
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class JwtTokenProvider(
    @Value("\${jwt.access.secret}") private val accessSecret: String,
    @Value("\${jwt.refresh.secret}") private val refreshSecret: String,
) {
    private val accessKey = Keys.hmacShaKeyFor(accessSecret.toByteArray())
    private val refreshKey = Keys.hmacShaKeyFor(refreshSecret.toByteArray())

    fun createAccessToken(id: Long): String {
        return createToken(id, accessKeyPair.private, SecurityConstants.ACCESS_TOKEN_EXPIRATION)
    }

    fun createRefreshToken(id: Long): String {
        return createToken(id, refreshKeyPair.private, SecurityConstants.REFRESH_TOKEN_EXPIRATION)
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

    fun validateAccessToken(token: String): Boolean {
        return validateToken(token, accessKey)
    }

    fun validateRefreshToken(token: String): Boolean {
        return validateToken(token, refreshKey)
    }

    private fun validateToken(token: String, key: SecretKey): Boolean {
        return try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getUserIdFromAccessToken(token: String): Long {
        return getUserIdFromToken(token, accessKey)
    }

    fun getUserIdFromRefreshToken(token: String): Long {
        return getUserIdFromToken(token, refreshKey)
    }

    private fun getUserIdFromToken(token: String, key: SecretKey): Long {
        val claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
        return claims.subject.toLong()
    }
}
