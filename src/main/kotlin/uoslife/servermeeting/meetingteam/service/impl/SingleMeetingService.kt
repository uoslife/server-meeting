package uoslife.servermeeting.meetingteam.service.impl

import java.util.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamUserListGetResponse
import uoslife.servermeeting.meetingteam.entity.Information
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.exception.InSingleMeetingTeamNoJoinTeamException
import uoslife.servermeeting.meetingteam.exception.InSingleMeetingTeamOnlyOneUserException
import uoslife.servermeeting.meetingteam.exception.InformationNotFoundException
import uoslife.servermeeting.meetingteam.exception.UserTeamNotFoundException
import uoslife.servermeeting.meetingteam.repository.MeetingTeamRepository
import uoslife.servermeeting.meetingteam.service.BaseMeetingService
import uoslife.servermeeting.meetingteam.service.util.MeetingServiceUtils
import uoslife.servermeeting.meetingteam.util.Validator
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.exception.UserNotFoundException
import uoslife.servermeeting.user.repository.UserRepository

@Service
@Transactional(readOnly = true)
@Qualifier("singleMeetingService")
class SingleMeetingService(
    private val userRepository: UserRepository,
    private val meetingTeamRepository: MeetingTeamRepository,
    private val meetingServiceUtils: MeetingServiceUtils,
    private val validator: Validator,
    @Value("\${app.season}") private val season: Int,
) : BaseMeetingService {

    @Transactional
    override fun createMeetingTeam(userUUID: UUID, name: String?, teamType: TeamType): String? {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()
        validator.isUserAlreadyHaveTeam(user)

        val meetingTeam = createDefaultMeetingTeam(leader = user, teamType = teamType)
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

        val meetingTeam = user.team ?: throw UserTeamNotFoundException()

        meetingTeam?.information = information
    }

    override fun getMeetingTeamInformation(userUUID: UUID): MeetingTeamInformationGetResponse {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()

        val meetingTeam = user.team

        val information = meetingTeam?.information ?: throw InformationNotFoundException()

        return meetingServiceUtils.toMeetingTeamInformationGetResponse(
            user.userPersonalInformation.gender,
            TeamType.SINGLE,
            listOf(user),
            information,
            null
        )
    }

    @Transactional
    override fun deleteMeetingTeam(userUUID: UUID) {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()

        val meetingTeam = user.team

        meetingTeamRepository.deleteById(meetingTeam?.id ?: throw UserTeamNotFoundException())
    }

    @Transactional
    fun createDefaultMeetingTeam(leader: User, teamType: TeamType): MeetingTeam {
        return meetingTeamRepository.save(
            MeetingTeam(
                season = season,
                code = "",
                leader = leader,
                type = teamType,
            ),
        )
    }
}
