package uoslife.servermeeting.verification.service

import uoslife.servermeeting.global.auth.dto.response.TokenResponse
import uoslife.servermeeting.verification.dto.request.VerificationCodeCheckRequest
import uoslife.servermeeting.verification.dto.request.VerificationCodeSendRequest
import uoslife.servermeeting.verification.dto.response.VerificationCodeSendResponse

interface VerificationService {
    fun sendMail(
        verificationCodeSendRequest: VerificationCodeSendRequest
    ): Unit
    fun verifyVerificationCode(
        verificationCodeCheckRequest: VerificationCodeCheckRequest
    ): TokenResponse
}
