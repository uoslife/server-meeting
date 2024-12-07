package uoslife.servermeeting.verification.service

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.amazonaws.services.simpleemail.model.*
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import uoslife.servermeeting.verification.exception.EmailDeliveryFailedException
import uoslife.servermeeting.verification.util.VerificationConstants
import uoslife.servermeeting.verification.util.VerificationUtils

@Service
class AsyncEmailService(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val sesClient: AmazonSimpleEmailService,
    private val templateEngine: TemplateEngine,
    @Value("\${auth.email.verification-code-expiry}") private val codeExpiry: Long,
    @Value("\${aws.ses.email.title}") private val emailTitle: String,
    @Value("\${aws.ses.email.from}") private val emailFrom: String
) {
    @Async
    fun sendEmailAsync(email: String): CompletableFuture<Unit> {
        return try {
            val verificationCode = VerificationUtils.generateVerificationCode()
            saveVerificationCode(email, verificationCode)
            sendEmail(email, verificationCode)
            incrementSendCount(email)
            CompletableFuture.completedFuture(Unit)
        } catch (e: Exception) {
            CompletableFuture.failedFuture(EmailDeliveryFailedException())
        }
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
        val context = Context()
        context.setVariable("expiryMinutes", codeExpiryMinutes)
        context.setVariable("verificationCode", verificationCode)

        val emailBody = templateEngine.process("email-template", context)

        val request =
            SendEmailRequest()
                .withDestination(Destination().withToAddresses(email))
                .withMessage(
                    Message()
                        .withSubject(Content(emailTitle))
                        .withBody(Body().withHtml(Content(emailBody)))
                )
                .withSource(emailFrom)
        //        sesClient.sendEmail(request)
    }
}
