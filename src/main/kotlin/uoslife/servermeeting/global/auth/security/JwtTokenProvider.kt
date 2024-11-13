package uoslife.servermeeting.global.auth.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class JwtTokenProvider(
    @Value("\${jwt.access.secret}") private val accessSecret: String,
    @Value("\${jwt.refresh.secret}") private val refreshSecret: String,
) {
    private val accessSecretKey = Keys.hmacShaKeyFor(accessSecret.toByteArray())
    private val refreshSecretKey = Keys.hmacShaKeyFor(refreshSecret.toByteArray())

    fun createAccessToken(id: Long): String {
        return createToken(id, accessSecretKey, SecurityConstants.ACCESS_TOKEN_EXPIRATION)
    }

    fun createRefreshToken(id: Long): String {
        return createToken(id, refreshSecretKey, SecurityConstants.REFRESH_TOKEN_EXPIRATION)
    }

    private fun createToken(id: Long, secretKey: Key, expiration: Long): String {
        val now = Date()
        val validity = Date(now.time + expiration)

        return Jwts.builder()
            .setSubject(id.toString())
            .setIssuer(SecurityConstants.TOKEN_ISSUER)
            .setAudience(SecurityConstants.TOKEN_AUDIENCE)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(secretKey)
            .compact()
    }

    fun validateAccessToken(token: String): Boolean {
        return validateToken(token, accessSecretKey)
    }

    fun validateRefreshToken(token: String): Boolean {
        return validateToken(token, refreshSecretKey)
    }

    private fun validateToken(token: String, secretKey: Key): Boolean {
        try {
            Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun getUserIdFromAccessToken(token: String): Long {
        return getUserIdFromToken(token, accessSecretKey)
    }

    fun getUserIdFromRefreshToken(token: String): Long {
        return getUserIdFromToken(token, refreshSecretKey)
    }

    private fun getUserIdFromToken(token: String, secretKey: Key): Long {
        val claims = Jwts.parser()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
        return claims.subject.toLong()
    }
}
