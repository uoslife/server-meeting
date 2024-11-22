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
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamUser
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamUserListGetResponse
import uoslife.servermeeting.meetingteam.dto.vo.MeetingTeamUsers
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.UserTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.exception.*
import uoslife.servermeeting.meetingteam.repository.MeetingTeamRepository
import uoslife.servermeeting.meetingteam.repository.PreferenceRepository
import uoslife.servermeeting.meetingteam.repository.UserTeamRepository
import uoslife.servermeeting.meetingteam.service.BaseMeetingService
import uoslife.servermeeting.meetingteam.service.util.MeetingServiceUtils
import uoslife.servermeeting.meetingteam.util.UniqueCodeGenerator
import uoslife.servermeeting.meetingteam.util.Validator
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.exception.UserNotFoundException
import uoslife.servermeeting.user.repository.UserRepository

@Service
@Transactional(readOnly = true)
@Qualifier("tripleMeetingService")
class TripleMeetingService(
    private val userRepository: UserRepository,
    private val userTeamDao: UserTeamDao,
    private val preferenceRepository: PreferenceRepository,
    private val uniqueCodeGenerator: UniqueCodeGenerator,
    private val meetingTeamRepository: MeetingTeamRepository,
    private val validator: Validator,
    private val userTeamRepository: UserTeamRepository,
    private val meetingServiceUtils: MeetingServiceUtils,
    @Value("\${app.season}") private val season: Int,
) : BaseMeetingService {

    @Transactional
    override fun createMeetingTeam(
        userId: Long,
        name: String?,
    ): MeetingTeamCodeResponse {
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()

        validator.isUserAlreadyHaveTripleTeam(user)
        validator.isTeamNameInvalid(name)

        val code = uniqueCodeGenerator.getUniqueTeamCode()
        val meetingTeam = createDefaultMeetingTeam(user, name, code, TeamType.TRIPLE)

        userTeamDao.saveUserTeam(meetingTeam, user, true)

        return MeetingTeamCodeResponse(code)
    }

    @Transactional
    override fun joinMeetingTeam(
        userId: Long,
        code: String,
        isJoin: Boolean
    ): MeetingTeamUserListGetResponse? {
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()

        validator.isTeamCodeValid(code)
        validator.isUserAlreadyHaveTripleTeam(user)

        val meetingTeam =
            meetingTeamRepository.findByCode(code) ?: throw MeetingTeamNotFoundException()

        validator.isTeamFull(meetingTeam)

        val newUserTeam = UserTeam.createUserTeam(meetingTeam, user, false)
        userTeamRepository.save(newUserTeam)

        return if (isJoin) {
            null
        } else {
            val meetingTeamUsers =
                MeetingTeamUsers(meetingTeam.userTeams.stream().map { it.user }.toList())
            meetingTeamUsers.toMeetingTeamUserListGetResponse(meetingTeam.name!!)
        }
    }

    override fun getMeetingTeamUserList(
        userId: Long,
        code: String
    ): MeetingTeamUserListGetResponse {
        userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        validator.isTeamCodeValid(code)

        val meetingTeam =
            meetingTeamRepository.findByCode(code) ?: throw MeetingTeamNotFoundException()

        val meetingTeamUsers = meetingTeam.userTeams.stream().map { it.user }.toList()
        return MeetingTeamUserListGetResponse(
            teamName = meetingTeam.name!!,
            userList =
                meetingTeamUsers.map {
                    MeetingTeamUser(
                        name = it.name,
                    )
                }
        )
    }

    @Transactional
    override fun updateMeetingTeamInfo(
        userId: Long,
        meetingTeamInfoUpdateRequest: MeetingTeamInfoUpdateRequest
    ) {
        validator.isMessageLengthIsValid(meetingTeamInfoUpdateRequest.message)

        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        val meetingTeam: MeetingTeam = getUserTripleMeetingUserTeam(user).team

        val newPreference = meetingTeamInfoUpdateRequest.toTriplePreference(meetingTeam)

        meetingTeam.preference?.let { preferenceRepository.delete(it) }
        meetingTeam.preference = newPreference
        meetingTeam.message = meetingTeamInfoUpdateRequest.message
    }

    override fun getMeetingTeamInformation(userId: Long): MeetingTeamInformationGetResponse {
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        val meetingTeam = getUserTripleMeetingUserTeam(user).team

        val preference = meetingTeam.preference ?: throw PreferenceNotFoundException()

        return meetingServiceUtils.toMeetingTeamInformationGetResponse(
            meetingTeam.gender,
            TeamType.TRIPLE,
            user,
            preference,
            meetingTeam.name,
            meetingTeam.message
        )
    }

    @Transactional
    override fun deleteMeetingTeam(userId: Long) {
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        val meetingUserTeam = getUserTripleMeetingUserTeam(user)

        if (!meetingUserTeam.isLeader) throw OnlyTeamLeaderCanDeleteTeamException()

        val meetingTeam = meetingUserTeam.team

        meetingTeam.userTeams.forEach { userTeamRepository.delete(it) }
        meetingTeamRepository.delete(meetingTeam)
    }

    @Transactional
    fun createDefaultMeetingTeam(
        leader: User,
        name: String?,
        code: String,
        teamType: TeamType
    ): MeetingTeam {
        return meetingTeamRepository.save(
            MeetingTeam(
                season = season,
                name = name,
                code = code,
                type = teamType,
                gender = leader.gender ?: throw GenderNotUpdatedException()
            ),
        )
    }

    private fun getUserTripleMeetingUserTeam(user: User): UserTeam {
        return userTeamDao.findByUserWithMeetingTeam(user, TeamType.TRIPLE)
            ?: throw MeetingTeamNotFoundException()
    }
}
