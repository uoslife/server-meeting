package uoslife.servermeeting.global.auth.service

import io.jsonwebtoken.Claims
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.global.auth.dto.response.TokenResponse
import uoslife.servermeeting.global.auth.exception.InvalidTokenException
import uoslife.servermeeting.global.auth.exception.UserNotFoundException
import uoslife.servermeeting.global.auth.jwt.TokenProvider
import uoslife.servermeeting.global.auth.jwt.TokenType
import uoslife.servermeeting.user.entity.User
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

        val user: User = userRepository.findByEmail(claims.subject) ?: throw UserNotFoundException()
        val tokenResponse: TokenResponse = verificationService.getTokenByEmail(user.email)

        return tokenResponse
    }

    //    @Transactional
    //    fun logout(userId: Long) {
    //        logger.info("logout user: $userId")
    //        val logoutUser = userRepository.findByIdOrNull(userId) ?: throw
    // UserNotFoundException()
    //        deviceService.deleteDeviceSecret(logoutUser.device?.id ?: throw
    // UnauthorizedException())
    //        smsVerificationRedisRepository.deleteById(logoutUser.phone)
    //    }

    //    @Transactional
    //    fun unregisterUser(userId: Long) {
    //        logger.info("unregister user: $userId")
    //        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
    //        deleteUserDeviceIfExists(user)
    //        deleteUserTosIfExists(user)
    //        deleteUserPortalAccountIfExists(user)
    //        userRepository.delete(user)
    //    }

    //    private fun setUserTos(tosDto: TosDto, user: User?) {
    //        user?.tos =
    //            tosRepository.save(
    //                Tos(
    //                    privatePolicy = tosDto.privatePolicy ?: false,
    //                    termsOfUse = tosDto.termsOfUse ?: false,
    //                    notification = tosDto.notification ?: false,
    //                    marketing = tosDto.marketing ?: false,
    //                )
    //            )
    //
    //        tosDto
    //            .takeIf { it.marketing == true }
    //            ?.let { subscribeToTopic(user, TopicName.MARKETING_NOTIFICATION.name) }
    //
    //        tosDto.takeIf { it.notification == true }?.let {
    // subscribeToAnnouncementNotification(user) }
    //    }
}
