package uoslife.servermeeting.global.auth.service

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import com.amazonaws.services.simpleemail.model.*
import jakarta.mail.internet.InternetAddress
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import uoslife.servermeeting.global.auth.dto.response.SendVerificationEmailResponse
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

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

    fun verifyCode(email: String, code: String) {
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
//            throw Exception("인증번호가 올바르지 않습니다.") // TODO: 알맞은 예외 클래스로 변경
        }
    }

    private fun validateSendCount(email: String) {
        val sendCountKey = generateRedisKey(SEND_COUNT_PREFIX, email, true)
        val currentCount = redisTemplate.opsForValue().get(sendCountKey)?.toString()?.toInt() ?: 0
        if (currentCount >= dailySendLimit) {
//            throw Exception("일일 최대 발송 횟수를 초과했습니다.") // TODO 알맞은 예외 클래스로 변경
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
        val request = SendEmailRequest()
            .withDestination(Destination().withToAddresses(email))
            .withMessage(
                Message()
                    .withSubject(Content(emailTitle))
                    .withBody(
                        Body().withText(
                            Content("인증 코드: $verificationCode")
                        )
                    )
            )
            .withSource(emailFrom)

        try {
            sesClient.sendEmail(request)
        } catch (e: Exception) {
//            throw Exception("메일 전송에 실패했습니다.") // TODO 알맞은 예외 클래스로 변경
        }
    }

    private fun calculateExpirationTime(): Long {
        return System.currentTimeMillis() + codeExpiry * 1000 // 현재 시간 + 유효 시간 (밀리초)
    }

    private fun validateEmail(email: String) {
        try {
            InternetAddress(email).validate()
            if (!email.endsWith(UOS_DOMAIN)) {
//                throw InvalidEmailDomainException("@uos.ac.kr 도메인만 허용됩니다.")
            }
        } catch (e: Exception) {
//            throw InvalidEmailFormatException("이메일 형식이 맞지 않습니다.")
        }
    }

    private fun validateVerificationAttempts(email: String) {
        val attemptsKey = generateRedisKey(VERIFY_COUNT_PREFIX, email, true)
        val attempts = redisTemplate.opsForValue().get(attemptsKey)?.toString()?.toInt() ?: 0
        if (attempts >= codeVerifyLimit) {
//            throw MaxVerificationAttemptsExceededException()
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
}
