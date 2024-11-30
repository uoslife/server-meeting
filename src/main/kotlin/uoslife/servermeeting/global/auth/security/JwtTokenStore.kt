package uoslife.servermeeting.global.auth.security

import java.util.concurrent.TimeUnit
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class JwtTokenStore(
    private val redisTemplate: RedisTemplate<String, Any>,
    @Value("\${jwt.refresh.expiration}") private val refreshTokenExpiration: Long,
) {
    fun saveRefreshToken(userId: Long, refreshToken: String) {
        val key = "${SecurityConstants.REFRESH_TOKEN_PREFIX}:$userId"
        redisTemplate
            .opsForValue()
            .set(key, refreshToken, refreshTokenExpiration, TimeUnit.MILLISECONDS)
    }

    fun getStoredRefreshToken(userId: Long): String? {
        val key = "${SecurityConstants.REFRESH_TOKEN_PREFIX}:$userId"
        return redisTemplate.opsForValue().get(key)?.toString()
    }

    fun deleteRefreshToken(userId: Long) {
        val key = "${SecurityConstants.REFRESH_TOKEN_PREFIX}:$userId"
        redisTemplate.delete(key)
    }
}
