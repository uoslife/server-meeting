package uoslife.servermeeting.global.auth.service

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import com.amazonaws.services.simpleemail.model.*
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
    @Value("\${auth.email.daily-send-limit}") private val dailySendLimit: Int,
    @Value("\${auth.email.verification-code-expiry}") private val codeExpiry: Long,
    @Value("\${aws.ses.email.title}") private val emailTitle: String,
    @Value("\${aws.ses.email.from}") private val emailFrom: String,
) {
    fun sendVerificationEmail(email: String): SendVerificationEmailResponse {
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

    private fun validateSendCount(email: String) {
        val sendCountKey = "email_send_count:$email:${LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)}"
        val currentCount = redisTemplate.opsForValue().get(sendCountKey)?.toString()?.toInt() ?: 0
        if (currentCount >= dailySendLimit) {
//            throw Exception("일일 최대 발송 횟수를 초과했습니다.") // TODO 알맞은 예외 클래스로 변경
        }
    }

    private fun generateVerificationCode(): String {
        return String.format("%04d", (0..9999).random())
    }

    private fun saveVerificationCode(email: String, verificationCode: String) {
        val codeKey = "email_verification_code:$email"
        redisTemplate.opsForValue().set(codeKey, verificationCode, codeExpiry, TimeUnit.SECONDS)
    }

    private fun incrementSendCount(email: String) {
        val sendCountKey = "email_send_count:$email:${LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)}"
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

}
