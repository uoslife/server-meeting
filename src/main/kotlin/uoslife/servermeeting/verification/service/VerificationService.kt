package uoslife.servermeeting.verification.service

import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import uoslife.servermeeting.verification.dto.request.VerificationRequest
import uoslife.servermeeting.verification.dto.request.VerificationCheckRequest
import uoslife.servermeeting.verification.entity.Verification
import uoslife.servermeeting.verification.exception.VerificationNotFoundException
import uoslife.servermeeting.verification.repository.VerificationRedisRepository
import uoslife.servermeeting.meetingteam.util.UniqueCodeGenerator

@Service
class VerificationService(
    private val verificationRedisRepository: VerificationRedisRepository,
    private val javaMailSender: JavaMailSender,
    private val uniqueCodeGenerator: UniqueCodeGenerator,
    @Value("\${mail.from}") private val mailFrom: String
) {
    fun sendMail(verificationRequest: VerificationRequest): Boolean {
        // 메일 전송 전 Redis에 중복되는 값이 있는지 확인헐 필요가 있을까?
//        val isExists: Boolean = verificationRedisRepository.existsByEmail(verificationRequest.email)

        // Certification 코드 생성
        val code: String = uniqueCodeGenerator.getUniqueCertCode()

        // DTO to entity
        val verification: Verification = Verification(verificationRequest.email, code)

        // db에 저장
        verificationRedisRepository.save(verification)

        // 메일 내용 생성
        val message: MimeMessage = javaMailSender.createMimeMessage()
        val messageHelper: MimeMessageHelper = MimeMessageHelper(message, true)

        messageHelper.setFrom(mailFrom)
        messageHelper.setTo(verificationRequest.email)
        messageHelper.setSubject("[시대팅] : 인증 메일 코드를 확인해주세요")
        messageHelper.setText(getVerificationMessage(code), true)

        // 메일 보내기
        javaMailSender.send(message)

        return true
    }

    private fun getVerificationMessage(code: String): String {
        return String.format("<h3 style='text-align:cetner;'>인증코드: %s</h3>", code)
    }

    fun checkVerificationCode(verificationCheckRequest: VerificationCheckRequest): Boolean {
        val matchedVerification: Verification = verificationRedisRepository.findByEmailAndCodeOrNull(verificationCheckRequest.email, verificationCheckRequest.code) ?: throw VerificationNotFoundException()
        matchedVerification.isVerified = true
        verificationRedisRepository.save(matchedVerification)

        return true
    }

    fun findByEmailAndIsVerified(email: String): Boolean {
        val isVerified: Boolean = verificationRedisRepository.existsByEmailAndIsVerified(email)
        return isVerified
    }
}
