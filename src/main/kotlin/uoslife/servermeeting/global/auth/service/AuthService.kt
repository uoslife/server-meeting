package uoslife.servermeeting.global.auth.service

import io.jsonwebtoken.Claims
import jakarta.servlet.http.HttpServletRequest
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.global.auth.dto.response.TokenResponse
import uoslife.servermeeting.global.auth.exception.InvalidTokenException
import uoslife.servermeeting.global.auth.jwt.TokenProvider
import uoslife.servermeeting.global.auth.jwt.TokenType
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.exception.UserNotFoundException
import uoslife.servermeeting.user.repository.UserRepository
import uoslife.servermeeting.user.service.UserService
import uoslife.servermeeting.verification.service.VerificationService

@Service
@Transactional(readOnly = true)
class AuthService(
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val tokenProvider: TokenProvider,
    private val verificationService: VerificationService,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(AuthService::class.java)
    }
    @Transactional
    fun refreshAccessToken(request: HttpServletRequest): TokenResponse {
        val refreshToken: String =
            tokenProvider.resolveToken(request) ?: throw InvalidTokenException()
        val claims: Claims = tokenProvider.parseClaims(refreshToken, TokenType.REFRESH_SECRET)

        val user: User =
            userRepository.findByIdOrNull(UUID.fromString(claims.subject))
                ?: throw UserNotFoundException()
        val tokenResponse: TokenResponse = verificationService.getTokenByUser(user)

        return tokenResponse
    }
}
