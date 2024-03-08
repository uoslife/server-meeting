package uoslife.servermeeting.global.auth.jwt

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash(value = "refreshToken", timeToLive = 1209600)
data class RefreshToken(
    @Id val userId: Long,
    val refreshToken: String,
)
