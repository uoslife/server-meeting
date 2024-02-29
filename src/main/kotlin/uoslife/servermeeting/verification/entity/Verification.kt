package uoslife.servermeeting.verification.entity

import java.time.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash(value = "verification", timeToLive = 300L) // 5분 cache
data class Verification(
    @Id val email: String,
    var code: String? = null, // 인증 코드
    var trialCount: Int = 0,
    var requestTime: LocalDateTime = LocalDateTime.now(),
    var isVerified: Boolean = false, // 인증 됐는지 확인
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
                isVerified = false,
            )
        }
    }
}
