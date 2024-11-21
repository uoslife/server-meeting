package uoslife.servermeeting.meetingteam.service.impl

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
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
    private val preferenceRepository: PreferenceRepository,
    private val validator: Validator,
    private val userTeamRepository: UserTeamRepository,
    private val userTeamDao: UserTeamDao,
    @Value("\${app.season}") private val season: Int,
) : BaseMeetingService {

    @Transactional
    override fun createMeetingTeam(
        userId: Long,
        name: String?,
    ): MeetingTeamCodeResponse {
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        validator.isUserAlreadyHaveSingleTeam(user)

        val meetingTeam = createDefaultMeetingTeam(leader = user, teamType = TeamType.SINGLE)

        val newUserTeam = UserTeam.createUserTeam(meetingTeam, user, true)

        userTeamRepository.save(newUserTeam)
        return MeetingTeamCodeResponse(code = null)
    }

    override fun joinMeetingTeam(
        userId: Long,
        code: String,
        isJoin: Boolean
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
        validator.isMessageLengthIsValid(meetingTeamInfoUpdateRequest.message)
        val validMBTI = validator.setValidMBTI(meetingTeamInfoUpdateRequest.mbti)

        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        val meetingTeam: MeetingTeam = getUserSingleMeetingTeam(user)

        val newPreference = meetingTeamInfoUpdateRequest.toSinglePreference(validMBTI, meetingTeam)

        meetingTeam.preference?.let { preferenceRepository.delete(it) }
        meetingTeam.preference = newPreference
        meetingTeam.message = meetingTeamInfoUpdateRequest.message
    }

    override fun getMeetingTeamInformation(userId: Long): MeetingTeamInformationGetResponse {
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        val meetingTeam: MeetingTeam = getUserSingleMeetingTeam(user)

        val preference = meetingTeam.preference ?: throw PreferenceNotFoundException()

        return meetingServiceUtils.toMeetingTeamInformationGetResponse(
            meetingTeam.gender,
            meetingTeam.type,
            user,
            preference,
            null,
            meetingTeam.message
        )
    }

    @Transactional
    override fun deleteMeetingTeam(userId: Long) {
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        val meetingTeam: MeetingTeam = getUserSingleMeetingTeam(user)

        meetingTeamRepository.delete(meetingTeam)
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
