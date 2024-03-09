package uoslife.servermeeting.meetingteam.service.impl

import java.util.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.meetingteam.dao.MeetingTeamDao
import uoslife.servermeeting.meetingteam.dto.request.MeetingTeamInformationUpdateRequest
import uoslife.servermeeting.meetingteam.dto.request.MeetingTeamPreferenceUpdateRequest
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamUser
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamUserListGetResponse
import uoslife.servermeeting.meetingteam.dto.vo.MeetingTeamUsers
import uoslife.servermeeting.meetingteam.entity.Information
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.exception.*
import uoslife.servermeeting.meetingteam.repository.MeetingTeamRepository
import uoslife.servermeeting.meetingteam.service.BaseMeetingService
import uoslife.servermeeting.meetingteam.service.util.MeetingServiceUtils
import uoslife.servermeeting.meetingteam.util.UniqueCodeGenerator
import uoslife.servermeeting.meetingteam.util.Validator
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.entity.enums.GenderType
import uoslife.servermeeting.user.exception.UserNotFoundException
import uoslife.servermeeting.user.repository.UserRepository

@Service
@Transactional(readOnly = true)
@Qualifier("tripleMeetingService")
class TripleMeetingService(
    private val userRepository: UserRepository,
    private val meetingTeamDao: MeetingTeamDao,
    private val meetingTeamRepository: MeetingTeamRepository,
    private val uniqueCodeGenerator: UniqueCodeGenerator,
    private val validator: Validator,
    private val meetingServiceUtils: MeetingServiceUtils,
    @Value("\${app.season}") private val season: Int,
) : BaseMeetingService {

    @Transactional
    override fun createMeetingTeam(userUUID: UUID, name: String?, teamType: TeamType): String? {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()

        validator.isUserAlreadyHaveTeam(user)
        validator.isTeamNameInvalid(name)

        val code = uniqueCodeGenerator.getUniqueTeamCode()
        val meetingTeam = saveMeetingTeam(user, name, code)

        user.team = meetingTeam
        return code
    }

    @Transactional
    override fun joinMeetingTeam(
        userUUID: UUID,
        code: String,
        isJoin: Boolean
    ): MeetingTeamUserListGetResponse? {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()

        validator.isTeamCodeValid(code)
        validator.isUserAlreadyHaveTeam(user)

        val meetingTeam =
            meetingTeamRepository.findByCode(code) ?: throw MeetingTeamNotFoundException()
        val leader = meetingTeam.leader ?: throw TeamLeaderNotFoundException()

        validator.isTeamFull(meetingTeam)
        validator.isUserSameGenderWithTeamLeader(user, leader)

        user.team = meetingTeam

        return if (isJoin) {
            null
        } else {
            val meetingTeamUsers = MeetingTeamUsers(meetingTeam.users)
            meetingTeamUsers.toMeetingTeamUserListGetResponse(meetingTeam.name!!)
        }
    }

    override fun getMeetingTeamUserList(
        userUUID: UUID,
        code: String
    ): MeetingTeamUserListGetResponse {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()
        validator.isTeamCodeValid(code)

        val meetingTeam =
            meetingTeamRepository.findByCode(code) ?: throw MeetingTeamNotFoundException()

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
        userUUID: UUID,
        meetingTeamInformationUpdateRequest: MeetingTeamInformationUpdateRequest
    ) {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()

        val userTeam =
            meetingTeamDao.findByUserWithMeetingTeam(user, TeamType.TRIPLE)
                ?: throw MeetingTeamNotFoundException()

        val information =
            Information(
                gender = user.userPersonalInformation.gender,
                meetingTeamInformationUpdateRequest.toMap()
            )
        userTeam.information = information
    }

    @Transactional
    override fun updateMeetingTeamPreference(
        userUUID: UUID,
        meetingTeamPreferenceUpdateRequest: MeetingTeamPreferenceUpdateRequest
    ) {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()

        val meetingTeam =
            meetingTeamDao.findByUserWithMeetingTeam(user, TeamType.TRIPLE)
                ?: throw MeetingTeamNotFoundException()

        val preference = meetingTeamPreferenceUpdateRequest.toTriplePreference()

        meetingTeam?.preference = preference
    }

    override fun getMeetingTeamInformation(userUUID: UUID): MeetingTeamInformationGetResponse {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()

        val meetingTeam =
            meetingTeamDao.findByUserWithMeetingTeam(user, TeamType.TRIPLE)
                ?: throw MeetingTeamNotFoundException()
        val userList = meetingTeam.users

        val information = meetingTeam.information ?: throw InformationNotFoundException()
        val preference = meetingTeam?.preference ?: throw PreferenceNotFoundException()

        return meetingServiceUtils.toMeetingTeamInformationGetResponse(
            user.userPersonalInformation?.gender ?: GenderType.MALE,
            TeamType.TRIPLE,
            userList,
            information,
            preference,
            meetingTeam.name
        )
    }

    @Transactional
    override fun deleteMeetingTeam(userUUID: UUID) {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()

        val meetingTeam =
            meetingTeamDao.findByUserWithMeetingTeam(user, TeamType.TRIPLE)
                ?: throw MeetingTeamNotFoundException()

        meetingTeamRepository.delete(meetingTeam)
    }

    @Transactional
    fun saveMeetingTeam(leader: User, name: String?, code: String): MeetingTeam {
        return meetingTeamRepository.save(
            MeetingTeam(
                season = season,
                name = name,
                code = code,
                type = TeamType.TRIPLE,
                leader = leader,
            ),
        )
    }
}
