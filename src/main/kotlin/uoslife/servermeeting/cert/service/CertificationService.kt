package uoslife.servermeeting.cert.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import uoslife.servermeeting.cert.dto.request.CertifyRequest
import uoslife.servermeeting.cert.dto.request.VerifyCodeRequest
import uoslife.servermeeting.cert.entity.Certification
import uoslife.servermeeting.cert.exception.CertificationNotFoundException
import uoslife.servermeeting.cert.repository.CertificationRepository
import uoslife.servermeeting.meetingteam.util.UniqueCodeGenerator

@Service
class CertificationService(
    private val certificationRepository: CertificationRepository,
    private val javaMailSender: JavaMailSender,
    private val uniqueCodeGenerator: UniqueCodeGenerator,
    @Value("\${mail.from}")
    private val mailFrom: String
) {
    fun sendMail(certifyRequest: CertifyRequest): Boolean {
        // 코드 생성, 옮길 예정
        val code: String = uniqueCodeGenerator.getUniqueCertCode()

        // DTO to entity
        var certification: Certification = Certification(null, certifyRequest.email, certifyRequest.university, code)

        // db에 저장, redis로 바꿔야 하나???
        certificationRepository.save(certification)

        // 메일 내용 생성
        var message: SimpleMailMessage = SimpleMailMessage()
        message.from = mailFrom
        message.setTo(certifyRequest.email)
        message.subject = "Uoslife : 인증 메일 코드를 확인해주세요"
        message.text = "인증 번호 : " + code

        // 메일 보내기
        javaMailSender.send(message)

        return true
    }

    fun verifyCode(verifyCodeRequest: VerifyCodeRequest): Boolean {
        val certification: Certification =
            certificationRepository.findByEmailAndCodeAndIsVerifiedNot(
                verifyCodeRequest.email,
                verifyCodeRequest.code
            )
                ?: throw CertificationNotFoundException()
        certification.isVerified = true // 인증 된 Cert로 변경
        certificationRepository.save(certification) // DB에 저장

        return true
    }

    fun findByEmailAndIsVerified(email: String): Boolean {
        val isVerifiedStatus: Boolean = certificationRepository.existsByEmailAndIsVerified(email)
        return isVerifiedStatus
    }
}
