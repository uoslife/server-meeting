package uoslife.servermeeting.global.auth.jwt

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
import java.util.Date
import javax.crypto.SecretKey
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import uoslife.servermeeting.global.auth.dto.response.TokenResponse
import uoslife.servermeeting.global.auth.exception.InvalidTokenException
import uoslife.servermeeting.global.auth.security.JwtUserDetailsService
import uoslife.servermeeting.user.entity.User

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

    private final val authorizationHeader: String = "Authorization"
    private final val bearerPrefix: String = "Bearer "

    fun getTokenSecret(tokenType: TokenType): String {
        return when (tokenType) {
            TokenType.ACCESS_SECRET -> accessTokenSecret
            TokenType.REFRESH_SECRET -> refreshTokenSecret
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
        val claims = parseClaims(accessToken, TokenType.ACCESS_SECRET)
        val principal: JwtUserDetails = jwtUserDetailsService.loadUserByUsername(claims.subject)
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
            .signWith(key(TokenType.ACCESS_SECRET), SignatureAlgorithm.HS256)
            .compact()
    }

    fun generateRefreshTokenFromUserPrincipal(
        userPrincipal: JwtUserDetails,
    ): String {
        return Jwts.builder()
            .setSubject(userPrincipal.username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + refreshTokenExpiration))
            .signWith(key(TokenType.REFRESH_SECRET), SignatureAlgorithm.HS256)
            .compact()
    }

    fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(authorizationHeader)
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(bearerPrefix)) {
            return bearerToken.substring(7)
        }
        return null
    }

    fun getTokenByUser(user: User): TokenResponse {
        val userDetails: JwtUserDetails =
            jwtUserDetailsService.loadUserByUsername(user.id.toString())

        val accessToken: String = generateAccessTokenFromUserPrincipal(userDetails)
        val refreshToken: String = generateRefreshTokenFromUserPrincipal(userDetails)

        return TokenResponse(accessToken = accessToken, refreshToken = refreshToken)
    }
}
