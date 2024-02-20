package uoslife.servermeeting.cert.service

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import uoslife.servermeeting.cert.dto.request.CertifyRequest
import uoslife.servermeeting.cert.dto.request.VerifyCodeRequest
import uoslife.servermeeting.cert.dto.response.VerifyCodeResponse
import uoslife.servermeeting.cert.entity.Cert
import uoslife.servermeeting.cert.exception.CertNotFoundException
import uoslife.servermeeting.cert.repository.CertRepository
import uoslife.servermeeting.meetingteam.util.UniqueCodeGenerator

@Service
class CertService(
    private val certRepository: CertRepository,
    private val javaMailSender: JavaMailSender,
    private val uniqueCodeGenerator: UniqueCodeGenerator
) {
    fun sendMail(
        certifyRequest: CertifyRequest
    ): Boolean {
        // 코드 생성, 옮길 예정
        val code: String = uniqueCodeGenerator.getUniqueCertCode()

        // DTO to entity
        var cert: Cert = Cert(null, certifyRequest.email, certifyRequest.university, code)

        // db에 저장, redis로 바꿔야 하나???
        certRepository.save(cert)

        // 메일 내용 생성
        var message: SimpleMailMessage = SimpleMailMessage()
        message.from = "gustmd5715@gmail.com"
        message.setTo(certifyRequest.email)
        message.subject = "Uoslife : 인증 메일 코드를 확인해주세요"
        message.text = "인증 번호 : " + code

        // 메일 보내기
        javaMailSender.send(message)

        return true
    }
}
