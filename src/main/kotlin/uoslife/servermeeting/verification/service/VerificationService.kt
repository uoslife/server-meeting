package uoslife.servermeeting.verification.service

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.amazonaws.services.simpleemail.model.*
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.global.auth.dto.response.TokenResponse
import uoslife.servermeeting.global.auth.jwt.TokenProvider
import uoslife.servermeeting.meetingteam.util.UniqueCodeGenerator
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.repository.UserRepository
import uoslife.servermeeting.verification.dto.University
import uoslife.servermeeting.verification.dto.request.VerificationCodeCheckRequest
import uoslife.servermeeting.verification.dto.request.VerificationCodeSendRequest
import uoslife.servermeeting.verification.dto.response.VerificationCodeSendResponse
import uoslife.servermeeting.verification.entity.Verification
import uoslife.servermeeting.verification.exception.VerificationCodeNotMatchException
import uoslife.servermeeting.verification.repository.VerificationRedisRepository

@Service
class VerificationService(
    private val verificationRedisRepository: VerificationRedisRepository,
    private val userRepository: UserRepository,
    private val amazonSimpleEmailService: AmazonSimpleEmailService,
    private val uniqueCodeGenerator: UniqueCodeGenerator,
    private val tokenProvider: TokenProvider,
    @Value("\${spring.mail.username}") private val mailFrom: String
) {
    companion object{
        const val SUBJECT: String = "[시대팅] 인증 메일 코드를 확인해주세요"
    }
    @Transactional
    fun sendMail(
        verificationCodeSendRequest: VerificationCodeSendRequest
    ): VerificationCodeSendResponse {
        // verification을 DB에 이미 존재하면 재발급하고, 없으면 새로 생성함
        val verification: Verification = getOrCreateVerification(verificationCodeSendRequest.email)

        // Vertification 코드 생성 후 주입
        val code: String = uniqueCodeGenerator.getUniqueVerificationCode()
        verification.resetCode(code)

        // Cache에 인증코드 저장
        verificationRedisRepository.save(verification)

        // 메일 내용 생성
        val destination: Destination = Destination().withToAddresses(listOf(verificationCodeSendRequest.email))
        val message: Message = Message().withSubject(createContent(SUBJECT)).withBody(Body().withHtml(createContent(getVerificationMessage(code))))
        val sendEmailRequest: SendEmailRequest = SendEmailRequest().withSource(mailFrom).withDestination(destination).withMessage(message)

        // 메일 보내기
        amazonSimpleEmailService.sendEmail(sendEmailRequest)

        return VerificationCodeSendResponse(true)
    }

    private fun createContent(content: String): Content {
        return Content().withData(content).withCharset("UTF-8")
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
    fun verifyVerificationCode(
        verificationCodeCheckRequest: VerificationCodeCheckRequest
    ): TokenResponse {
        // 유저 업데이트 하거나 새로 생성해주는 마스터 코드(000000)
        if (verificationCodeCheckRequest.code.equals("000000")) {
            val savedUser: User =
                getOrCreateUser(
                    verificationCodeCheckRequest.email,
                    verificationCodeCheckRequest.university
                )
            return tokenProvider.getTokenByUser(savedUser)
        }

        // 리퀘스트 인증코드와 DB 인증코드가 같은지 체크
        matchVerificationCode(verificationCodeCheckRequest.email, verificationCodeCheckRequest.code)

        // 캐시에 인증코드 삭제
        verificationRedisRepository.deleteById(verificationCodeCheckRequest.email)

        // 유저 반환, 새로운 유저면 회원가입 후 반환
        val savedUser: User =
            getOrCreateUser(
                verificationCodeCheckRequest.email,
                verificationCodeCheckRequest.university
            )

        // token(accessToken, refreshToken) 발급
        val tokenResponse: TokenResponse = tokenProvider.getTokenByUser(savedUser)

        return tokenResponse
    }

    private fun matchVerificationCode(email: String, code: String): Unit {
        val matchedVerification: Verification =
            verificationRedisRepository.findByIdOrNull(email)
                ?: throw VerificationCodeNotMatchException()
        if (!matchedVerification.code.equals(code)) throw VerificationCodeNotMatchException()
    }

    private fun getOrCreateUser(email: String, university: University): User {
        val user: User =
            userRepository.findByEmail(email)
                ?: userRepository.save(User.create(email = email, university = university))

        return user
    }
}
