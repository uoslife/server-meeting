package uoslife.servermeeting.verification.service

import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.global.auth.jwt.TokenProvider
import uoslife.servermeeting.global.auth.security.JwtUserDetailsService
import uoslife.servermeeting.meetingteam.util.UniqueCodeGenerator
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.repository.UserRepository
import uoslife.servermeeting.verification.dto.University
import uoslife.servermeeting.verification.dto.request.VerificationCodeCheckRequest
import uoslife.servermeeting.verification.dto.request.VerificationCodeSendRequest
import uoslife.servermeeting.global.auth.dto.response.TokenResponse
import uoslife.servermeeting.verification.dto.response.VerificationCodeSendResponse
import uoslife.servermeeting.verification.entity.Verification
import uoslife.servermeeting.verification.exception.VerificationCodeNotMatchException
import uoslife.servermeeting.verification.exception.VerificationNotFoundException
import uoslife.servermeeting.verification.repository.VerificationRedisRepository

@Service
class VerificationService(
    private val verificationRedisRepository: VerificationRedisRepository,
    private val userRepository: UserRepository,
    private val javaMailSender: JavaMailSender,
    private val uniqueCodeGenerator: UniqueCodeGenerator,
    private val tokenProvider: TokenProvider,
    private val jwtUserDetailsService: JwtUserDetailsService,
    @Value("\${mail.from}") private val mailFrom: String
) {
    @Transactional
    fun sendMail(
        verificationCodeSendRequest: VerificationCodeSendRequest
    ): VerificationCodeSendResponse {
        // verification을 DB에 이미 존재하면 가져오고, 없으면 새로 생성함
        val verification: Verification = getOrCreateVerification(verificationCodeSendRequest.email)

        // Vertification 코드 생성 후 주입
        val code: String = uniqueCodeGenerator.getUniqueVerificationCode()
        verification.resetCode(code)

        // Cache에 인증코드 저장
        verificationRedisRepository.save(verification)

        // 메일 내용 생성
        val message: MimeMessage = createMail(verification.email, code)

        // 메일 보내기
        javaMailSender.send(message)

        return VerificationCodeSendResponse(true)
    }

    private fun createMail(email: String, code: String): MimeMessage {
        val message: MimeMessage = javaMailSender.createMimeMessage()
        val messageHelper: MimeMessageHelper = MimeMessageHelper(message, true)

        messageHelper.setFrom(mailFrom)
        messageHelper.setTo(email)
        messageHelper.setSubject("[시대팅] 인증 메일 코드를 확인해주세요")
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
    fun verifyVerificationCode(
        verificationCodeCheckRequest: VerificationCodeCheckRequest
    ): TokenResponse {
        // 유저 업데이트 하거나 새로 생성해주는 마스터 코드(000000)
        if(verificationCodeCheckRequest.code.equals("000000")){
            val savedUser: User = updateOrCreateUser(verificationCodeCheckRequest.email, verificationCodeCheckRequest.university)
            return getTokenByEmail(savedUser.email)
        }

        // 리퀘스트 인증코드와 DB 인증코드가 같은지 체크
        matchVerificationCode(verificationCodeCheckRequest.email, verificationCodeCheckRequest.code)

        // 인증코드 일치할 때, redis에 인증코드 지워야 하나?
//        verificationRedisRepository.deleteById(verificationCodeCheckRequest.email)

        // 유저 반환, 새로운 유저면 회원가입 후 반환
        val savedUser: User = updateOrCreateUser(verificationCodeCheckRequest.email, verificationCodeCheckRequest.university)

        // token(accessToken, refreshToken) 발급
        val tokenResponse: TokenResponse = getTokenByEmail(savedUser.email)

        return tokenResponse
    }

    private fun matchVerificationCode(email: String, code: String): Unit{
        val matchedVerification: Verification =
            verificationRedisRepository.findByIdOrNull(email)
                ?: throw VerificationNotFoundException()
        if (!matchedVerification.code.equals(code))
            throw VerificationCodeNotMatchException()
    }

    private fun updateOrCreateUser(email: String, university: University): User{
        val user: User = userRepository.findByEmail(email) ?: User.create(
            email = email,
            university = university
        )
        val savedUser: User = userRepository.save(user)

        return savedUser
    }

    fun getTokenByEmail(email: String): TokenResponse {
        val accessToken: String = tokenProvider.generateAccessTokenFromEmail(email)
        val refreshToken: String = tokenProvider.generateRefreshTokenFromEmail(email)

        return TokenResponse(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }
}
