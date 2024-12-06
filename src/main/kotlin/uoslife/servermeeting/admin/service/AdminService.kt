package uoslife.servermeeting.admin.service

import jakarta.servlet.http.HttpServletResponse
import java.time.Duration
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import uoslife.servermeeting.match.service.MatchingService
import uoslife.servermeeting.meetingteam.dao.UserTeamDao
import uoslife.servermeeting.global.common.dto.RequestInfoDto
import uoslife.servermeeting.payment.dto.response.PaymentResponseDto
import uoslife.servermeeting.payment.service.PaymentService
import uoslife.servermeeting.user.service.UserService
import uoslife.servermeeting.verification.util.VerificationConstants
import uoslife.servermeeting.verification.util.VerificationUtils

@Service
class AdminService(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val userService: UserService,
    @Qualifier("PortOneService") private val paymentService: PaymentService,
    private val matchingService: MatchingService,
    private val userTeamDao: UserTeamDao,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(AdminService::class.java)
    }

    fun resetEmailSendCount(email: String, requestInfo: RequestInfoDto) {
        val sendCountKey =
            VerificationUtils.generateRedisKey(VerificationConstants.SEND_COUNT_PREFIX, email, true)

        redisTemplate.opsForValue().set(sendCountKey, "0")
        redisTemplate.expire(sendCountKey, Duration.ofDays(1))

        logger.info("[ADMIN-이메일 발송 횟수 초기화] targetEmail: $email, $requestInfo")
    }

    fun deleteUserById(userId: Long, response: HttpServletResponse, requestInfo: RequestInfoDto) {
        userService.deleteUserById(userId, response)
        logger.info("[ADMIN-유저 삭제] targetUserId: $userId, $requestInfo")
    }

    fun refundPayment(
        requestInfo: RequestInfoDto
    ): PaymentResponseDto.NotMatchedPaymentRefundResponse {
        val result = paymentService.refundPayment()
        logger.info("[ADMIN-매칭 실패 유저 환불] $requestInfo")
        return result
    }

    @Async
    fun warmUpCacheAsync(season: Int) {
        logger.info("[캐시 웜업 시작]")
        val participants = userTeamDao.findAllParticipantsBySeasonAndType(season)
        participants.forEach { participant ->
            try {
                matchingService.getMatchInfo(participant.userId, participant.teamType, season)
            } catch (e: Exception) {
                logger.info("[캐시 웜업 실패] userId: ${participant.userId} message: ${e.message}")
            }
        }
        logger.info("[캐시 웜업 성공] 대상 인원: ${participants.size}")
    }
}
