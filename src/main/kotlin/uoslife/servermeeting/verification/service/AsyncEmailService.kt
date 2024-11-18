package uoslife.servermeeting.verification.service

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.amazonaws.services.simpleemail.model.*
import java.time.Duration
import java.util.concurrent.TimeUnit
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import uoslife.servermeeting.verification.exception.EmailDeliveryFailedException
import uoslife.servermeeting.verification.util.VerificationConstants
import uoslife.servermeeting.verification.util.VerificationUtils

@Service
class AsyncEmailService(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val sesClient: AmazonSimpleEmailService,
    @Value("\${auth.email.verification-code-expiry}") private val codeExpiry: Long,
    @Value("\${aws.ses.email.title}") private val emailTitle: String,
    @Value("\${aws.ses.email.from}") private val emailFrom: String
) {
    @Async
    fun sendEmailAsync(email: String) {
        val verificationCode = VerificationUtils.generateVerificationCode()
        saveVerificationCode(email, verificationCode)
        sendEmail(email, verificationCode)
        incrementSendCount(email)
    }

    private fun saveVerificationCode(email: String, verificationCode: String) {
        val codeKey = VerificationUtils.generateRedisKey(VerificationConstants.CODE_PREFIX, email)
        redisTemplate.opsForValue().set(codeKey, verificationCode, codeExpiry, TimeUnit.SECONDS)
    }

    private fun incrementSendCount(email: String) {
        val sendCountKey =
            VerificationUtils.generateRedisKey(VerificationConstants.SEND_COUNT_PREFIX, email, true)
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
