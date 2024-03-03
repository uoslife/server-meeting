package uoslife.servermeeting.verification.service

import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.meetingteam.util.UniqueCodeGenerator
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.repository.UserRepository
import uoslife.servermeeting.verification.dto.University
import uoslife.servermeeting.verification.dto.request.VerificationCheckRequest
import uoslife.servermeeting.verification.dto.request.VerificationRequest
import uoslife.servermeeting.verification.dto.response.VerificationCodeResponse
import uoslife.servermeeting.verification.entity.Verification
import uoslife.servermeeting.verification.exception.UniversityNotFoundException
import uoslife.servermeeting.verification.exception.VerificationNotFoundException
import uoslife.servermeeting.verification.exception.VerificationCodeNotMatchException
import uoslife.servermeeting.verification.repository.VerificationRedisRepository

@Service
@Transactional(readOnly = true)
class VerificationService(
    private val verificationRedisRepository: VerificationRedisRepository,
    private val userRepository: UserRepository,
    private val javaMailSender: JavaMailSender,
    private val uniqueCodeGenerator: UniqueCodeGenerator,
    @Value("\${mail.from}") private val mailFrom: String
) {
    @Transactional
    fun sendMail(verificationRequest: VerificationRequest): Boolean {
        // verification을 DB에 이미 존재하면 가져오고, 없으면 새로 생성함
        val verification: Verification = getOrCreateVerification(verificationRequest.email)

        // Vertification 코드 생성 후 주입
        val code: String = uniqueCodeGenerator.getUniqueVerificationCode()
        verification.resetCode(code)

        // Cache에 인증코드 저장
        verificationRedisRepository.save(verification)

        // 메일 내용 생성
        val message: MimeMessage = createMail(verification.email, code)

        // 메일 보내기
        javaMailSender.send(message)

        return true
    }

    private fun createMail(email: String, code: String): MimeMessage {
        val message: MimeMessage = javaMailSender.createMimeMessage()
        val messageHelper: MimeMessageHelper = MimeMessageHelper(message, true)

        messageHelper.setFrom(mailFrom)
        messageHelper.setTo(email)
        messageHelper.setSubject("[시대팅] : 인증 메일 코드를 확인해주세요")
        messageHelper.setText(getVerificationMessage(code), true)

        return message
    }

    private fun getOrCreateVerification(email: String): Verification {
        val verification: Verification =
            verificationRedisRepository.findByIdOrNull(email) ?: Verification.create(email)

        return verification
    }

    private fun getVerificationMessage(code: String): String {
        return String.format("<h3 style='text-align:cetner;'>인증코드: %s</h3>", code)
    }

    @Transactional
    fun checkVerificationCode(verificationCheckRequest: VerificationCheckRequest): VerificationCodeResponse {
        val matchedVerification: Verification =
            verificationRedisRepository.findByIdOrNull(verificationCheckRequest.email)
                ?: throw VerificationNotFoundException()

        // check request code and db code
        if (!matchedVerification.code.equals(verificationCheckRequest.code)) throw VerificationCodeNotMatchException()

        matchedVerification?.isVerified = true
        matchedVerification?.let { verificationRedisRepository.save(it) }

        val university: University = extractUniversity(verificationCheckRequest.email)

        // User DB에 저장
        val user: User = User.create(email = verificationCheckRequest.email, university = university)
        userRepository.save(user)

        // token 발급(security 나오면 추가 예정)
        val accessToken: String = ""

        return VerificationCodeResponse(accessToken)
    }

    private fun extractUniversity(email: String): University {
        val domain: String = email.split("@")[1]
        val discriminator: String = domain.split(".")[0]

        val university: University = University.values().filter { university: University -> university.name.equals(discriminator) }.getOrNull(0) ?: throw UniversityNotFoundException()
        return university
    }
}
