package uoslife.servermeeting.meetingteam.service.impl

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.meetingteam.dao.UserTeamDao
import uoslife.servermeeting.meetingteam.dto.request.MeetingTeamInformationUpdateRequest
import uoslife.servermeeting.meetingteam.dto.request.MeetingTeamMessageUpdateRequest
import uoslife.servermeeting.meetingteam.dto.request.MeetingTeamPreferenceUpdateRequest
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamCodeResponse
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamUserListGetResponse
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.UserTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.exception.*
import uoslife.servermeeting.meetingteam.repository.MeetingTeamRepository
import uoslife.servermeeting.meetingteam.repository.UserTeamRepository
import uoslife.servermeeting.meetingteam.service.BaseMeetingService
import uoslife.servermeeting.meetingteam.service.util.MeetingServiceUtils
import uoslife.servermeeting.meetingteam.util.Validator
import uoslife.servermeeting.user.dao.UserDao
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.exception.UserNotFoundException
import uoslife.servermeeting.user.repository.UserRepository

@Service
@Transactional(readOnly = true)
@Qualifier("singleMeetingService")
class SingleMeetingService(
    private val userRepository: UserRepository,
    private val meetingTeamRepository: MeetingTeamRepository,
    private val userDao: UserDao,
    private val meetingServiceUtils: MeetingServiceUtils,
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
        validator.isUserAlreadyHaveTeam(user)

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
    override fun updateMeetingTeamInformation(
        userId: Long,
        meetingTeamInformationUpdateRequest: MeetingTeamInformationUpdateRequest
    ) {
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        val userTeam: UserTeam =
            userTeamDao.findByUserWithMeetingTeam(user, TeamType.SINGLE)
                ?: throw MeetingTeamNotFoundException()
        val meetingTeam = userTeam.team

        val information =
            meetingTeamInformationUpdateRequest.toInformation(
                user.gender ?: throw GenderNotFoundException()
            )

        meetingTeam.information = information
    }

    @Transactional
    override fun updateMeetingTeamPreference(
        userId: Long,
        meetingTeamPreferenceUpdateRequest: MeetingTeamPreferenceUpdateRequest
    ) {
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        val meetingTeam: MeetingTeam = getUserSingleMeetingTeam(user)

        val validMBTI = validator.setValidMBTI(meetingTeamPreferenceUpdateRequest.mbti)
        val preference = meetingTeamPreferenceUpdateRequest.toSinglePreference(validMBTI)

        meetingTeam.preference = preference
    }

    @Transactional
    override fun updateMeetingTeamMessage(
        userId: Long,
        meetingTeamMessageUpdateRequest: MeetingTeamMessageUpdateRequest
    ) {
        validator.isMessageLengthIsValid(meetingTeamMessageUpdateRequest.message)

        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        val meetingTeam: MeetingTeam = getUserSingleMeetingTeam(user)

        val message = meetingTeamMessageUpdateRequest.message

        meetingTeam.message = message
    }

    override fun getMeetingTeamInformation(userId: Long): MeetingTeamInformationGetResponse {
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        val meetingTeam: MeetingTeam = getUserSingleMeetingTeam(user)

        val information = meetingTeam.information ?: throw InformationNotFoundException()
        val preference = meetingTeam.preference ?: throw PreferenceNotFoundException()

        return meetingServiceUtils.toMeetingTeamInformationGetResponse(
            user.gender ?: throw GenderNotFoundException(),
            TeamType.SINGLE,
            user,
            information,
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
            ),
        )
    }

    private fun getUserSingleMeetingTeam(user: User): MeetingTeam {
        val userTeam: UserTeam =
            userTeamDao.findByUserWithMeetingTeam(user, TeamType.SINGLE)
                ?: throw MeetingTeamNotFoundException()
        return userTeam.team
    }
}
