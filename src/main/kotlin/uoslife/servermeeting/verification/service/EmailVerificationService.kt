package uoslife.servermeeting.verification.service

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.amazonaws.services.simpleemail.model.*
import jakarta.mail.internet.AddressException
import jakarta.mail.internet.InternetAddress
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import uoslife.servermeeting.verification.dto.response.SendVerificationEmailResponse
import uoslife.servermeeting.verification.exception.*

@Service
class EmailVerificationService(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val sesClient: AmazonSimpleEmailService,
    @Value("\${auth.email.verification-code-expiry}") private val codeExpiry: Long,
    @Value("\${auth.email.daily-send-limit}") private val dailySendLimit: Int,
    @Value("\${auth.email.code-verify-limit}") private val codeVerifyLimit: Int,
    @Value("\${aws.ses.email.title}") private val emailTitle: String,
    @Value("\${aws.ses.email.from}") private val emailFrom: String,
) {
    companion object {
        private const val CODE_PREFIX = "email_verification_code:"
        private const val SEND_COUNT_PREFIX = "email_send_count"
        private const val VERIFY_COUNT_PREFIX = "verification_attempts:"
        private const val UOS_DOMAIN = "@uos.ac.kr"
    }

    fun sendVerificationEmail(email: String): SendVerificationEmailResponse {
        // 이메일 형식 검증
        validateEmail(email)
        // 발송 제한 확인
        validateSendCount(email)
        // 인증 코드 생성 및 저장
        val verificationCode = generateVerificationCode()
        saveVerificationCode(email, verificationCode)
        // Redis에 발송 횟수 증가
        incrementSendCount(email)
        // 이메일 전송
        sendEmail(email, verificationCode)
        // 코드 만료 시각 계산
        val expirationTime = calculateExpirationTime()

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
        val verificationCodeKey = generateRedisKey(CODE_PREFIX, email)
        return redisTemplate.opsForValue().get(verificationCodeKey).toString()
    }

    private fun validateVerificationCode(redisCode: String, code: String) {
        if (redisCode != code) {
            throw EmailVerificationCodeMismatchException()
        }
    }

    private fun validateSendCount(email: String) {
        val sendCountKey = generateRedisKey(SEND_COUNT_PREFIX, email, true)
        val currentCount = redisTemplate.opsForValue().get(sendCountKey)?.toString()?.toInt() ?: 0
        if (currentCount >= dailySendLimit) {
            throw DailyEmailSendLimitExceededException()
        }
    }

    private fun generateVerificationCode(): String {
        return String.format("%04d", (0..9999).random())
    }

    private fun saveVerificationCode(email: String, verificationCode: String) {
        val codeKey = generateRedisKey(CODE_PREFIX, email)
        redisTemplate.opsForValue().set(codeKey, verificationCode, codeExpiry, TimeUnit.SECONDS)
    }

    private fun incrementSendCount(email: String) {
        val sendCountKey = generateRedisKey(SEND_COUNT_PREFIX, email, true)
        redisTemplate.opsForValue().increment(sendCountKey)
        redisTemplate.expire(sendCountKey, Duration.ofDays(1))
    }

    private fun sendEmail(email: String, verificationCode: String) {
        val codeExpiryMinutes = codeExpiry / 60
        val emailBody = String.format(emailTemplate, codeExpiryMinutes, verificationCode)
        val request =
            SendEmailRequest()
                .withDestination(Destination().withToAddresses(email))
                .withMessage(
                    Message()
                        .withSubject(Content(emailTitle))
                        .withBody(Body().withHtml(Content(emailBody)))
                )
                .withSource(emailFrom)
        try {
            sesClient.sendEmail(request)
        } catch (e: Exception) {
            throw EmailDeliveryFailedException()
        }
    }

    private fun calculateExpirationTime(): Long {
        return System.currentTimeMillis() + codeExpiry * 1000 // 현재 시간 + 유효 시간 (밀리초)
    }

    private fun validateEmail(email: String) {
        try {
            InternetAddress(email).validate()
            if (!email.endsWith(UOS_DOMAIN)) {
                throw InvalidEmailDomainException()
            }
        } catch (e: AddressException) {
            throw InvalidEmailFormatException()
        }
    }

    private fun validateVerificationAttempts(email: String) {
        val attemptsKey = generateRedisKey(VERIFY_COUNT_PREFIX, email, true)
        val attempts = redisTemplate.opsForValue().get(attemptsKey)?.toString()?.toInt() ?: 0
        if (attempts >= codeVerifyLimit) {
            throw DailyVerificationAttemptLimitExceededException()
        }
    }

    private fun incrementVerificationAttempts(email: String) {
        val attemptsKey = generateRedisKey(VERIFY_COUNT_PREFIX, email, true)
        redisTemplate.opsForValue().increment(attemptsKey)
        redisTemplate.expire(attemptsKey, Duration.ofDays(1))
    }

    private fun clearVerificationData(email: String) {
        val codeKey = generateRedisKey(CODE_PREFIX, email)
        val attemptsKey = generateRedisKey(VERIFY_COUNT_PREFIX, email, true)
        redisTemplate.delete(codeKey)
        redisTemplate.delete(attemptsKey)
    }

    private fun generateRedisKey(prefix: String, email: String, isDate: Boolean = false): String {
        if (!isDate) {
            return "$prefix:$email"
        }
        return "$prefix:$email:${LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)}"
    }

    private val emailTemplate =
        """
        <html>
            <body style="font-family: Arial, sans-serif;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <div style="display: flex; align-items: center; gap: 10px; margin-bottom: 30px;">
                        <img alt="Logo" src="https://www.uoslife.team/favicon.svg" width="40" height="40" />
                        <span style="font-size: 19px; font-weight: bold;">시대생</span>
                    </div>

                    <h1 style="font-size: 34px; margin-bottom: 20px;">인증번호 안내</h1>
                    <p style="color: #575757; font-size: 16px;">인증번호의 유효시간은 <strong>%d분</strong>입니다.</p>

                    <div style="border: 2px solid #d1d1d1; padding: 35px; text-align: center; margin: 30px 0;">
                        <strong style="font-size: 40px;">%s</strong>
                    </div>

                    <footer style="background: #f1f1f1; padding: 25px; margin-top: 30px;">
                        <a href="https://www.instagram.com/uoslife_official/" style="color: #575757; text-decoration: none; margin-right: 20px; font-size: 12px;">Instagram</a>
                        <a href="https://www.uoslife.team/" style="color: #575757; text-decoration: none; font-size: 12px;">Blog</a>
                        <p style="margin-top: 10px; font-size: 12px; color: #575757;">© 2024 시대생팀</p>
                    </footer>
                </div>
            </body>
        </html>
    """.trimIndent(
        )
}
