package uoslife.servermeeting.verification.service

import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import uoslife.servermeeting.meetingteam.util.UniqueCodeGenerator
import uoslife.servermeeting.verification.dto.request.VerificationCheckRequest
import uoslife.servermeeting.verification.dto.request.VerificationRequest
import uoslife.servermeeting.verification.entity.Verification
import uoslife.servermeeting.verification.exception.VerificationNotFoundException
import uoslife.servermeeting.verification.repository.VerificationRedisRepository

@Service
@Transactional(readOnly = true)
class VerificationService(
    private val verificationRedisRepository: VerificationRedisRepository,
    private val javaMailSender: JavaMailSender,
    private val uniqueCodeGenerator: UniqueCodeGenerator,
    @Value("\${mail.from}") private val mailFrom: String
) {
    @Transactional
    fun sendMail(verificationRequest: VerificationRequest): Boolean {
        // verification을 DB에 이미 존재하면 가져오고, 없으면 새로 생성함
//        val verification: Verification = getOrCreateVerification(verificationRequest.email)

        // Vertification 코드 생성 후 주입
        val verification: Verification = Verification.create(email = verificationRequest.email)
        val code: String = uniqueCodeGenerator.getUniqueVerificationCode()
        verification.resetCode(code)

        // DB에 저장
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
    private fun getOrCreateVerification(email: String): Verification {
        val verification: Verification = verificationRedisRepository.findByEmail(email)

        return verification
//        return verificationRedisRepository.findByEmailOrNull(email) ?: Verification.create(email)
    }

    private fun getVerificationMessage(code: String): String {
        return String.format("<h3 style='text-align:cetner;'>인증코드: %s</h3>", code)
    }

    fun checkVerificationCode(verificationCheckRequest: VerificationCheckRequest): Boolean {
        val matchedVerification: Verification = verificationRedisRepository.findByIdOrNull(verificationCheckRequest.email) ?: throw VerificationNotFoundException()

        // check request code and db code
        if(!matchedVerification.code.equals(verificationCheckRequest.code))
            return false

        matchedVerification.isVerified = true
        verificationRedisRepository.save(matchedVerification)

        return true
    }

    fun findByEmailAndIsVerified(email: String): Boolean {
        val isVerified: Boolean = verificationRedisRepository.existsByEmailAndIsVerified(email)
        return isVerified
    }
}
