package uoslife.servermeeting.verification.entity

import java.time.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash(value = "verification", timeToLive = 3600L) // 1시간 cache
data class Verification(
    @Id val email: String,
    var code: String? = null, // 인증 코드
    var trialCount: Int = 0,
    var requestTime: LocalDateTime = LocalDateTime.now(),
) {
    fun increaseTrialCount() {
        this.trialCount += 1
    }

    fun resetCode(code: String) {
        this.code = code
        this.requestTime = LocalDateTime.now()
    }

    companion object {
        fun create(email: String): Verification {
            return Verification(
                email = email,
                trialCount = 1,
                requestTime = LocalDateTime.now(),
            )
        }
    }
}
