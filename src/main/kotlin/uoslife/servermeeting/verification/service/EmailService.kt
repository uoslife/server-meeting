package uoslife.servermeeting.verification.service

import software.amazon.awssdk.services.sesv2.model.SendEmailResponse

interface EmailService {
    fun sendEmail(to: String, code: String): SendEmailResponse
}
