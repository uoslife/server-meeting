package uoslife.servermeeting.meetingteam.service.impl

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.meetingteam.dao.UserTeamDao
import uoslife.servermeeting.meetingteam.dto.request.MeetingTeamInfoUpdateRequest
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamCodeResponse
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamUserListGetResponse
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.UserTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.exception.*
import uoslife.servermeeting.meetingteam.repository.MeetingTeamRepository
import uoslife.servermeeting.meetingteam.repository.PreferenceRepository
import uoslife.servermeeting.meetingteam.repository.UserTeamRepository
import uoslife.servermeeting.meetingteam.service.BaseMeetingService
import uoslife.servermeeting.meetingteam.service.util.MeetingServiceUtils
import uoslife.servermeeting.meetingteam.util.Validator
import uoslife.servermeeting.payment.service.PaymentService
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.service.UserService

@Service
@Transactional(readOnly = true)
@Qualifier("singleMeetingService")
class SingleMeetingService(
    private val userService: UserService,
    private val meetingTeamRepository: MeetingTeamRepository,
    private val meetingServiceUtils: MeetingServiceUtils,
    private val preferenceRepository: PreferenceRepository,
    private val validator: Validator,
    private val userTeamRepository: UserTeamRepository,
    private val userTeamDao: UserTeamDao,
    @Qualifier("PortOneService") private val paymentService: PaymentService,
    @Value("\${app.season}") private val season: Int,
) : BaseMeetingService {
    companion object {
        private val logger = LoggerFactory.getLogger(SingleMeetingService::class.java)
    }

    @Transactional
    override fun createMeetingTeam(
        userId: Long,
    ): MeetingTeamCodeResponse {
        val user = userService.getUser(userId)
        validator.isUserAlreadyHaveSingleTeam(user)

        val meetingTeam = createDefaultMeetingTeam(leader = user, teamType = TeamType.SINGLE)

        val newUserTeam = UserTeam.createUserTeam(meetingTeam, user, true)

        userTeamRepository.save(newUserTeam)
        logger.info("[유저 팀 생성] User ID : $userId Single Team ID : ${meetingTeam.id}")
        return MeetingTeamCodeResponse(code = null)
    }

    override fun joinMeetingTeam(
        userId: Long,
        code: String,
    ): MeetingTeamUserListGetResponse? {
        throw InSingleMeetingTeamNoJoinTeamException()
    }

    override fun getMeetingTeamUserList(
        userId: Long,
        code: String
    ): MeetingTeamUserListGetResponse {
        throw InSingleMeetingTeamOnlyOneUserException()
    }

    @Transactional
    override fun updateMeetingTeamInfo(
        userId: Long,
        meetingTeamInfoUpdateRequest: MeetingTeamInfoUpdateRequest
    ) {
        //        validator.isMessageLengthIsValid(meetingTeamInfoUpdateRequest.course)
        val validMBTI = validator.setValidMBTI(meetingTeamInfoUpdateRequest.mbti)

        val user = userService.getUser(userId)
        val meetingTeam: MeetingTeam = getUserSingleMeetingTeam(user)
        val newPreference = meetingTeamInfoUpdateRequest.toSinglePreference(validMBTI, meetingTeam)

        meetingTeam.preference?.let {
            meetingTeam.preference = null
            preferenceRepository.delete(it)
            preferenceRepository.flush()
        } // 분리된 트랜잭션 호출

        meetingTeam.preference = newPreference
        meetingTeam.course = meetingTeamInfoUpdateRequest.course
    }

    override fun getMeetingTeamInformation(userId: Long): MeetingTeamInformationGetResponse {
        val user = userService.getUser(userId)
        val meetingTeam: MeetingTeam = getUserSingleMeetingTeam(user)

        val preference = meetingTeam.preference ?: throw PreferenceNotFoundException()

        return meetingServiceUtils.toMeetingTeamInformationGetResponse(
            meetingTeam.gender,
            meetingTeam.type,
            user,
            preference,
            null,
            meetingTeam.course
        )
    }

    @Transactional
    override fun deleteMeetingTeam(userId: Long) {
        val user = userService.getUser(userId)
        val meetingTeam: MeetingTeam = getUserSingleMeetingTeam(user)

        checkTeamPayment(user)
        meetingTeamRepository.delete(meetingTeam)
        logger.info("[1:1 미팅 팀 삭제] User ID : $userId")
    }

    private fun checkTeamPayment(user: User) {
        val successPayment =
            paymentService.getSuccessPayment(userId = user.id!!, teamType = TeamType.SINGLE)

        if (successPayment != null) {
            paymentService.refundPaymentByToken(user.id!!, TeamType.SINGLE)
        }

        user.payments?.forEach { payment -> payment.removeMeetingTeam() }
    }

    @Transactional
    fun createDefaultMeetingTeam(leader: User, teamType: TeamType): MeetingTeam {
        return meetingTeamRepository.save(
            MeetingTeam(
                season = season,
                type = teamType,
                gender = leader.gender ?: throw GenderNotUpdatedException()
            )
        )
    }

    private fun getUserSingleMeetingTeam(user: User): MeetingTeam {
        val userTeam: UserTeam =
            userTeamDao.findByUserWithMeetingTeam(user, TeamType.SINGLE)
                ?: throw MeetingTeamNotFoundException()
        return userTeam.team
    }
}
