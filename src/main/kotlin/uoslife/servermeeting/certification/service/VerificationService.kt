package uoslife.servermeeting.certification.service

import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import uoslife.servermeeting.certification.dto.request.VerificationRequest
import uoslife.servermeeting.certification.dto.request.VerificationCheckRequest
import uoslife.servermeeting.certification.entity.Certification
import uoslife.servermeeting.certification.exception.VerificationNotFoundException
import uoslife.servermeeting.certification.repository.VerificationRepository
import uoslife.servermeeting.meetingteam.util.UniqueCodeGenerator

@Service
class VerificationService(
    private val verificationRepository: VerificationRepository,
    private val javaMailSender: JavaMailSender,
    private val uniqueCodeGenerator: UniqueCodeGenerator,
    @Value("\${mail.from}") private val mailFrom: String
) {
    fun sendMail(verificationRequest: VerificationRequest): Boolean {
        // Certification 코드 생성
        val code: String = uniqueCodeGenerator.getUniqueCertCode()

        // DTO to entity
        val certification: Certification =
            Certification(null, verificationRequest.email, verificationRequest.university, code)

        // db에 저장
        verificationRepository.save(certification)

        // 메일 내용 생성
        val message: MimeMessage = javaMailSender.createMimeMessage()
        val messageHelper: MimeMessageHelper = MimeMessageHelper(message, true)

        messageHelper.setFrom(mailFrom)
        messageHelper.setTo(verificationRequest.email)
        messageHelper.setSubject("[시대팅] : 인증 메일 코드를 확인해주세요")
        messageHelper.setText(getCertificationMessage(code), true)

        // 메일 보내기
        javaMailSender.send(message)

        return true
    }

    private fun getCertificationMessage(code: String): String {
        return String.format("<h3 style='text-align:cetner;'>인증코드: %s</h3>", code)
    }

    fun verifyCode(verificationCheckRequest: VerificationCheckRequest): Boolean {
        val certification: Certification =
            verificationRepository.findByEmailAndCodeAndIsVerifiedNot(
                verificationCheckRequest.email,
                verificationCheckRequest.code
            )
                ?: throw VerificationNotFoundException()
        certification.isVerified = true // 인증 된 Cert로 변경
        verificationRepository.save(certification) // 변경 사항 DB에 저장

        return true
    }

    fun findByEmailAndIsVerified(email: String): Boolean {
        val isVerifiedStatus: Boolean = verificationRepository.existsByEmailAndIsVerified(email)
        return isVerifiedStatus
    }
}
