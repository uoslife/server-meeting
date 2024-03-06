package com.uoslife.core.auth.jwt

import com.uoslife.core.auth.exception.InvalidTokenException
import com.uoslife.core.auth.jwt.TokenType.ACCESS_SECRET
import com.uoslife.core.auth.jwt.TokenType.REFRESH_SECRET
import com.uoslife.core.auth.security.JwtUserDetails
import com.uoslife.core.auth.security.JwtUserDetailsService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.http.HttpServletRequest
import java.util.Base64
import java.util.Date
import javax.crypto.SecretKey
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

@Component
class TokenProvider(
    @Value("\${jwt.access.secret}") private val accessTokenSecret: String,
    @Value("\${jwt.access.expiration}") private val accessTokenExpiration: Long,
    @Value("\${jwt.refresh.secret}") private val refreshTokenSecret: String,
    @Value("\${jwt.refresh.expiration}") private val refreshTokenExpiration: Long,
    private val jwtUserDetailsService: JwtUserDetailsService,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(TokenProvider::class.java)
    }

    private val authorizationHeader: String = "Authorization"
    private val bearerPrefix: String = "Bearer "

    fun getTokenSecret(tokenType: TokenType): String {
        return when (tokenType) {
            ACCESS_SECRET -> accessTokenSecret
            REFRESH_SECRET -> refreshTokenSecret
        }
    }

    fun key(type: TokenType): SecretKey? {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(getTokenSecret(type)))
    }

    fun validateJwtToken(authToken: String, type: TokenType): Boolean {
        try {
            Jwts.parserBuilder().setSigningKey(key(type)).build().parseClaimsJws(authToken)
            return true
        } catch (e: MalformedJwtException) {
            logger.info("Invalid JWT token: {}", e.message)
        } catch (e: ExpiredJwtException) {
            logger.info("JWT token is expired: {}", e.message)
        } catch (e: UnsupportedJwtException) {
            logger.info("JWT token is unsupported: {}", e.message)
        } catch (e: IllegalArgumentException) {
            logger.info("JWT claims string is empty: {}", e.message)
        } catch (e: SignatureException) {
            logger.info("JWT signature does not match: {}", e.message)
        }
        throw InvalidTokenException()
    }

    fun getAuthentication(accessToken: String): UsernamePasswordAuthenticationToken {
        val claims = parseClaims(accessToken, ACCESS_SECRET)
        val principal: JwtUserDetails = jwtUserDetailsService.loadUserByUsername(claims.subject)
        return UsernamePasswordAuthenticationToken(principal, "", principal.authorities)
    }

    fun getTempAuthentication(tempToken: String): UsernamePasswordAuthenticationToken {
        val claims = parseClaims(tempToken, REFRESH_SECRET)
        val principal: UserDetails =
            JwtUserDetails(
                id = String(Base64.getDecoder().decode(claims.subject)),
                authorities = null,
            )
        return UsernamePasswordAuthenticationToken(principal, "", principal.authorities)
    }

    fun parseClaims(accessToken: String, type: TokenType): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(key(type))
            .build()
            .parseClaimsJws(accessToken)
            .body
    }

    fun generateAccessTokenFromUserPrincipal(userPrincipal: JwtUserDetails): String {
        logger.info("generate access token for user: ${userPrincipal.username}")
        return Jwts.builder()
            .setSubject(userPrincipal.username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + accessTokenExpiration))
            .signWith(key(ACCESS_SECRET), SignatureAlgorithm.HS256)
            .compact()
    }

    fun generateTempTokenFromSMSVerification(phoneNumber: String): String {
        logger.info("generate temp token for phone number: $phoneNumber")
        return Jwts.builder()
            .setSubject(Base64.getEncoder().encodeToString(phoneNumber.toByteArray()))
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + accessTokenExpiration))
            .signWith(key(REFRESH_SECRET), SignatureAlgorithm.HS256)
            .compact()
    }

    fun generateRefreshTokenFromUserPrincipal(
        userPrincipal: JwtUserDetails,
        deviceSecret: String,
        deviceId: Long
    ): String {
        return Jwts.builder()
            .setSubject(userPrincipal.username)
            .setAudience(deviceSecret)
            .setIssuer(deviceId.toString())
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + refreshTokenExpiration))
            .signWith(key(REFRESH_SECRET), SignatureAlgorithm.HS256)
            .compact()
    }

    fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(authorizationHeader)
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(bearerPrefix)) {
            return bearerToken.substring(7)
        }
        return null
    }

    fun shouldRefreshToken(claims: Claims): Boolean {
        return claims.expiration.time - System.currentTimeMillis() < refreshTokenExpiration / 2
    }
}
