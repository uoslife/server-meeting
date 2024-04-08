package uoslife.servermeeting.verification.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.sesv2.SesV2Client
import software.amazon.awssdk.services.sesv2.model.*
import uoslife.servermeeting.verification.exception.EmailSendFailedException

@Service
class EmailServiceImpl(
    private val sesV2Client: SesV2Client,
    @Value("\${cloud.aws.ses.from}") private val mailFrom: String
) : EmailService {

    companion object {
        const val SUBJECT: String = "[시대팅] 인증 메일 코드를 확인해주세요"
        private val logger = LoggerFactory.getLogger(EmailServiceImpl::class.java)
    }

    override fun sendEmail(destination: String, code: String): SendEmailResponse {
        // 메일 내용 생성
        val sendEmailRequest: SendEmailRequest = createSendEmailRequest(destination, code)

        // 메일 보내기
        try {
            val sendEmailResponse: SendEmailResponse = sesV2Client.sendEmail(sendEmailRequest)
            logger.info("Verification mail is sended from $mailFrom to ${destination}")

            return sendEmailResponse
        } catch (e: SesV2Exception) {
            logger.error("Email Send Failed Exception")
            logger.error(e.message)
            throw EmailSendFailedException()
        } catch (e: Exception) {
            logger.error("Email Send Failed Exception")
            logger.error(e.message)
            throw EmailSendFailedException()
        }
    }

    private fun createSendEmailRequest(destination: String, code: String): SendEmailRequest {
        // 수신 이메일 설정
        val destination: Destination = Destination.builder().toAddresses(destination).build()

        // 메일 내용 설정
        val emailContent: EmailContent = createEmailContent(code)

        val sendEmailRequest: SendEmailRequest =
            SendEmailRequest.builder()
                .fromEmailAddress(mailFrom)
                .destination(destination)
                .content(emailContent)
                .build()

        return sendEmailRequest
    }

    private fun createContent(content: String): Content {
        return Content.builder().data(content).charset("UTF-8").build()
    }

    private fun createEmailContent(code: String): EmailContent {
        val message: Message =
            Message.builder()
                .subject(createContent(SUBJECT))
                .body(Body.builder().html(createContent(getVerificationMessage(code))).build())
                .build()

        return EmailContent.builder().simple(message).build()
    }

    private fun getVerificationMessage(code: String): String {
        return String.format("<h3 style='text-align:cetner;'>인증코드: %s</h3>", code)
    }
}
