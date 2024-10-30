package uoslife.servermeeting.meetingteam.service.impl

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.meetingteam.dto.request.MeetingTeamInformationUpdateRequest
import uoslife.servermeeting.meetingteam.dto.request.MeetingTeamMessageUpdateRequest
import uoslife.servermeeting.meetingteam.dto.request.MeetingTeamPreferenceUpdateRequest
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamCodeResponse
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamUser
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamUserListGetResponse
import uoslife.servermeeting.meetingteam.dto.vo.MeetingTeamUsers
import uoslife.servermeeting.meetingteam.entity.TripleMeetingTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.exception.*
import uoslife.servermeeting.meetingteam.repository.TripleMeetingTeamRepository
import uoslife.servermeeting.meetingteam.service.BaseMeetingService
import uoslife.servermeeting.meetingteam.service.util.MeetingServiceUtils
import uoslife.servermeeting.meetingteam.util.UniqueCodeGenerator
import uoslife.servermeeting.meetingteam.util.Validator
import uoslife.servermeeting.user.dao.UserDao
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.exception.UserNotFoundException
import uoslife.servermeeting.user.repository.UserRepository

@Service
@Transactional(readOnly = true)
@Qualifier("tripleMeetingService")
class TripleMeetingService(
    private val userRepository: UserRepository,
    private val userDao: UserDao,
    private val tripleMeetingTeamRepository: TripleMeetingTeamRepository,
    private val uniqueCodeGenerator: UniqueCodeGenerator,
    private val validator: Validator,
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
        val meetingTeam = saveMeetingTeam(user, name, code)

        user.tripleTeam = meetingTeam
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
            tripleMeetingTeamRepository.findByCode(code) ?: throw MeetingTeamNotFoundException()
        val leader = meetingTeam.leader ?: throw TeamLeaderNotFoundException()

        validator.isTeamFull(meetingTeam)
        validator.isUserSameGenderWithTeamLeader(user, leader)

        user.tripleTeam = meetingTeam

        return if (isJoin) {
            null
        } else {
            val meetingTeamUsers = MeetingTeamUsers(meetingTeam.users)
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
            tripleMeetingTeamRepository.findByCode(code) ?: throw MeetingTeamNotFoundException()

        val meetingTeamUsers = meetingTeam.users
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
    override fun updateMeetingTeamInformation(
        userId: Long,
        meetingTeamInformationUpdateRequest: MeetingTeamInformationUpdateRequest
    ) {
        val user = userDao.findUserWithMeetingTeam(userId) ?: throw UserNotFoundException()
        val meetingTeam = user.tripleTeam ?: throw MeetingTeamNotFoundException()

        val information = meetingTeamInformationUpdateRequest.toInformation(user.gender)

        meetingTeam.information = information
    }

    @Transactional
    override fun updateMeetingTeamPreference(
        userId: Long,
        meetingTeamPreferenceUpdateRequest: MeetingTeamPreferenceUpdateRequest
    ) {
        val user = userDao.findUserWithMeetingTeam(userId) ?: throw UserNotFoundException()
        val meetingTeam = user.tripleTeam ?: throw MeetingTeamNotFoundException()

        val preference = meetingTeamPreferenceUpdateRequest.toTriplePreference()

        meetingTeam.preference = preference
    }

    @Transactional
    override fun updateMeetingTeamMessage(
        userId: Long,
        meetingTeamMessageUpdateRequest: MeetingTeamMessageUpdateRequest
    ) {
        validator.isMessageLengthIsValid(meetingTeamMessageUpdateRequest.message)
        val user = userDao.findUserWithMeetingTeam(userId) ?: throw UserNotFoundException()
        val meetingTeam = user.tripleTeam ?: throw MeetingTeamNotFoundException()

        val message = meetingTeamMessageUpdateRequest.message

        meetingTeam.message = message
    }

    override fun getMeetingTeamInformation(userId: Long): MeetingTeamInformationGetResponse {
        val user = userDao.findUserWithMeetingTeam(userId) ?: throw UserNotFoundException()
        val meetingTeam = user.tripleTeam ?: throw MeetingTeamNotFoundException()

        val userList = meetingTeam.users

        val information = meetingTeam.information ?: throw InformationNotFoundException()
        val preference = meetingTeam.preference ?: throw PreferenceNotFoundException()

        return meetingServiceUtils.toMeetingTeamInformationGetResponse(
            user.gender,
            TeamType.TRIPLE,
            user,
            information,
            preference,
            meetingTeam.name,
            meetingTeam.message
        )
    }

    @Transactional
    override fun deleteMeetingTeam(userId: Long) {
        val user = userDao.findUserWithMeetingTeam(userId) ?: throw UserNotFoundException()
        val meetingTeam = user.tripleTeam ?: throw MeetingTeamNotFoundException()
        meetingTeam.users.forEach { it.tripleTeam = null }

        tripleMeetingTeamRepository.delete(meetingTeam)
    }

    @Transactional
    fun saveMeetingTeam(leader: User, name: String?, code: String): TripleMeetingTeam {
        return tripleMeetingTeamRepository.save(
            TripleMeetingTeam(
                season = season,
                name = name,
                code = code,
                leader = leader,
            ),
        )
    }
}
