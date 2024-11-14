package uoslife.servermeeting.global.auth.security

import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*

@Component
class JwtTokenProvider(
    @Value("\${jwt.access.secret}") private val accessSecret: String,
    @Value("\${jwt.refresh.secret}") private val refreshSecret: String,
) {
    private val accessKeyPair = Jwts.SIG.ES256.keyPair().build()
    private val refreshKeyPair = Jwts.SIG.ES256.keyPair().build()

    fun createAccessToken(id: Long): String {
        return createToken(id, accessKeyPair.private, SecurityConstants.ACCESS_TOKEN_EXPIRATION)
    }

    fun createRefreshToken(id: Long): String {
        return createToken(id, refreshKeyPair.private, SecurityConstants.REFRESH_TOKEN_EXPIRATION)
    }

    private fun createToken(id: Long, privateKey: PrivateKey, expiration: Long): String {
        val now = Date()
        val validity = Date(now.time + expiration)

        return Jwts.builder()
            .subject(id.toString())
            .issuer(SecurityConstants.TOKEN_ISSUER)
            .audience().add(SecurityConstants.TOKEN_AUDIENCE).and()
            .issuedAt(now)
            .expiration(validity)
            .signWith(privateKey, Jwts.SIG.ES256)
            .compact()
    }

    fun validateAccessToken(token: String): Boolean {
        return validateToken(token, accessKeyPair.public)
    }

    fun validateRefreshToken(token: String): Boolean {
        return validateToken(token, refreshKeyPair.public)
    }

    private fun validateToken(token: String, publicKey: PublicKey): Boolean {
        try {
            Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun getUserIdFromAccessToken(token: String): Long {
        return getUserIdFromToken(token, accessKeyPair.public)
    }

    fun getUserIdFromRefreshToken(token: String): Long {
        return getUserIdFromToken(token, refreshKeyPair.public)
    }

    private fun getUserIdFromToken(token: String, publicKey: PublicKey): Long {
        val claims = Jwts.parser()
            .verifyWith(publicKey)
            .build()
            .parseSignedClaims(token)
            .payload
        return claims.subject.toLong()
    }
}
