package uoslife.servermeeting.domain.meeting.domain.service.impl

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.domain.meeting.application.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.domain.meeting.application.response.MeetingTeamUserListGetResponse
import uoslife.servermeeting.domain.meeting.domain.dao.UserTeamDao
import uoslife.servermeeting.domain.meeting.domain.entity.Information
import uoslife.servermeeting.domain.meeting.domain.entity.MeetingTeam
import uoslife.servermeeting.domain.meeting.domain.entity.UserTeam
import uoslife.servermeeting.domain.meeting.domain.entity.enums.TeamType
import uoslife.servermeeting.domain.meeting.domain.exception.InSingleMeetingTeamNoJoinTeamException
import uoslife.servermeeting.domain.meeting.domain.exception.InSingleMeetingTeamOnlyOneUserException
import uoslife.servermeeting.domain.meeting.domain.exception.InformationNotFoundException
import uoslife.servermeeting.domain.meeting.domain.exception.UserTeamNotFoundException
import uoslife.servermeeting.domain.meeting.domain.repository.MeetingTeamRepository
import uoslife.servermeeting.domain.meeting.domain.service.BaseMeetingService
import uoslife.servermeeting.domain.meeting.domain.service.util.MeetingServiceUtils
import uoslife.servermeeting.domain.meeting.domain.util.Validator
import uoslife.servermeeting.domain.user.domain.entity.enums.GenderType
import uoslife.servermeeting.domain.user.domain.exception.UserNotFoundException
import uoslife.servermeeting.domain.user.domain.repository.UserRepository
import java.util.UUID

@Service
@Transactional(readOnly = true)
@Qualifier("singleMeetingService")
class SingleMeetingService(
    private val userRepository: UserRepository,
    private val meetingTeamRepository: MeetingTeamRepository,
    private val userTeamDao: UserTeamDao,
    private val meetingServiceUtils: MeetingServiceUtils,
    private val validator: Validator,
    @Value("\${app.season}") private val season: Int,
) : BaseMeetingService {

    @Transactional
    override fun createMeetingTeam(userUUID: UUID, name: String?): String? {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()
        validator.isUserAlreadyHaveTeam(user)

        val meetingTeam = createDefaultMeetingTeam()
        val newUserTeam = UserTeam.createUserTeam(meetingTeam, user, true, TeamType.SINGLE)
        userTeamDao.saveUserTeam(newUserTeam)
        return ""
    }

    override fun joinMeetingTeam(
        userUUID: UUID,
        code: String,
        isJoin: Boolean
    ): MeetingTeamUserListGetResponse? {
        throw InSingleMeetingTeamNoJoinTeamException()
    }

    override fun getMeetingTeamUserList(
        userUUID: UUID,
        code: String
    ): MeetingTeamUserListGetResponse {
        throw InSingleMeetingTeamOnlyOneUserException()
    }

    @Transactional
    override fun updateMeetingTeamInformation(
        userUUID: UUID,
        information: Information,
    ) {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()

        val userTeam =
            userTeamDao.findByUserWithMeetingTeam(user, TeamType.SINGLE)
                ?: throw UserTeamNotFoundException()
        val meetingTeam = userTeam.team

        meetingTeam.information = information
    }

    override fun getMeetingTeamInformation(userUUID: UUID): MeetingTeamInformationGetResponse {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()

        val userTeam =
            userTeamDao.findByUserWithMeetingTeam(user, TeamType.SINGLE)
                ?: throw UserTeamNotFoundException()
        val meetingTeam = userTeam.team

        val information = meetingTeam.information ?: throw InformationNotFoundException()

        return meetingServiceUtils.toMeetingTeamInformationGetResponse(
            user.userPersonalInformation?.gender ?: GenderType.MALE,
            TeamType.SINGLE,
            listOf(user),
            information,
            null
        )
    }

    @Transactional
    override fun deleteMeetingTeam(userUUID: UUID) {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()

        val userTeam =
            userTeamDao.findByUserWithMeetingTeam(user, TeamType.SINGLE)
                ?: throw UserTeamNotFoundException()
        val meetingTeam = userTeam.team

        meetingTeamRepository.deleteById(meetingTeam.id!!)
    }

    @Transactional
    fun createDefaultMeetingTeam(): MeetingTeam {
        return meetingTeamRepository.save(
            MeetingTeam(
                season = season,
                code = "",
            ),
        )
    }
}
