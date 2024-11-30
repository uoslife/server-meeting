package uoslife.servermeeting.admin.service

import java.time.Duration
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import uoslife.servermeeting.verification.util.VerificationConstants
import uoslife.servermeeting.verification.util.VerificationUtils

@Service
class AdminEmailVerificationService(
    private val redisTemplate: RedisTemplate<String, Any>,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(AdminEmailVerificationService::class.java)
    }

    fun resetEmailSendCount(email: String) {
        val sendCountKey =
            VerificationUtils.generateRedisKey(VerificationConstants.SEND_COUNT_PREFIX, email, true)

        redisTemplate.opsForValue().set(sendCountKey, "0")
        redisTemplate.expire(sendCountKey, Duration.ofDays(1))

        logger.info("[이메일 발송 횟수 초기화] email: $email")
    }
}
