package uoslife.servermeeting.verification.service

import jakarta.mail.internet.AddressException
import jakarta.mail.internet.InternetAddress
import java.time.Duration
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import uoslife.servermeeting.verification.dto.response.SendVerificationEmailResponse
import uoslife.servermeeting.verification.exception.*
import uoslife.servermeeting.verification.util.VerificationConstants
import uoslife.servermeeting.verification.util.VerificationUtils

@Service
class EmailVerificationService(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val asyncEmailService: AsyncEmailService,
    @Value("\${auth.email.verification-code-expiry}") private val codeExpiry: Long,
    @Value("\${auth.email.daily-send-limit}") private val dailySendLimit: Int,
    @Value("\${auth.email.code-verify-limit}") private val codeVerifyLimit: Int,
) {

    fun sendVerificationEmail(email: String): SendVerificationEmailResponse {
        // 이메일 형식 검증
        validateEmail(email)
        // 발송 제한 확인
        validateSendCount(email)
        // 비동기 이메일 전송
        asyncEmailService.sendEmailAsync(email)
        // 코드 만료 시각 계산
        val expirationTime = VerificationUtils.calculateExpirationTime(codeExpiry)

        return SendVerificationEmailResponse(
            expirationTime = expirationTime,
            validDuration = codeExpiry
        )
    }

    fun verifyEmail(email: String, code: String) {
        validateVerificationAttempts(email)
        incrementVerificationAttempts(email)
        // Redis에서 인증 코드 조회
        val redisCode = getVerificationCode(email)
        // 인증 코드 검증
        validateVerificationCode(redisCode, code)
        // 검증 성공한 코드 삭제
        clearVerificationData(email)
    }

    private fun getVerificationCode(email: String): String {
        val verificationCodeKey =
            VerificationUtils.generateRedisKey(VerificationConstants.CODE_PREFIX, email)
        return redisTemplate.opsForValue().get(verificationCodeKey).toString()
    }

    private fun validateVerificationCode(redisCode: String, code: String) {
        if (redisCode != code) {
            throw EmailVerificationCodeMismatchException()
        }
    }

    private fun validateSendCount(email: String) {
        val sendCountKey =
            VerificationUtils.generateRedisKey(VerificationConstants.SEND_COUNT_PREFIX, email, true)
        val currentCount = redisTemplate.opsForValue().get(sendCountKey)?.toString()?.toInt() ?: 0
        if (currentCount >= dailySendLimit) {
            throw DailyEmailSendLimitExceededException()
        }
    }

    private fun validateEmail(email: String) {
        try {
            InternetAddress(email).validate()
            if (!email.endsWith(VerificationConstants.UOS_DOMAIN)) {
                throw InvalidEmailDomainException()
            }
        } catch (e: AddressException) {
            throw InvalidEmailFormatException()
        }
    }

    private fun validateVerificationAttempts(email: String) {
        val attemptsKey =
            VerificationUtils.generateRedisKey(
                VerificationConstants.VERIFY_COUNT_PREFIX,
                email,
                true
            )
        val attempts = redisTemplate.opsForValue().get(attemptsKey)?.toString()?.toInt() ?: 0
        if (attempts >= codeVerifyLimit) {
            throw DailyVerificationAttemptLimitExceededException()
        }
    }

    private fun incrementVerificationAttempts(email: String) {
        val attemptsKey =
            VerificationUtils.generateRedisKey(
                VerificationConstants.VERIFY_COUNT_PREFIX,
                email,
                true
            )
        redisTemplate.opsForValue().increment(attemptsKey)
        redisTemplate.expire(attemptsKey, Duration.ofDays(1))
    }

    private fun clearVerificationData(email: String) {
        val codeKey = VerificationUtils.generateRedisKey(VerificationConstants.CODE_PREFIX, email)
        val attemptsKey =
            VerificationUtils.generateRedisKey(
                VerificationConstants.VERIFY_COUNT_PREFIX,
                email,
                true
            )
        redisTemplate.delete(codeKey)
        redisTemplate.delete(attemptsKey)
    }
}
