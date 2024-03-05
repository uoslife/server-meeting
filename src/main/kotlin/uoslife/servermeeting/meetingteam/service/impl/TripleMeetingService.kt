package uoslife.servermeeting.meetingteam.service.impl

import java.util.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamUser
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamUserListGetResponse
import uoslife.servermeeting.meetingteam.dto.vo.MeetingTeamUsers
import uoslife.servermeeting.meetingteam.entity.Information
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.exception.InformationNotFoundException
import uoslife.servermeeting.meetingteam.exception.MeetingTeamNotFoundException
import uoslife.servermeeting.meetingteam.exception.TeamLeaderNotFoundException
import uoslife.servermeeting.meetingteam.exception.UserTeamNotFoundException
import uoslife.servermeeting.meetingteam.repository.MeetingTeamRepository
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
        validator.isTeamNameLeast2Character(name)

        val code = uniqueCodeGenerator.getUniqueTeamCode()
        val meetingTeam = saveMeetingTeam(user, name, code)

        user.team = meetingTeam
        userRepository.save(user)
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
                        nickname = it.nickname,
                        age = it.userPersonalInformation.birthYear
                    )
                }
        )
    }

    @Transactional
    override fun updateMeetingTeamInformation(
        userUUID: UUID,
        information: Information,
    ) {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()

        val userTeam = user.team ?: throw UserTeamNotFoundException()
        userTeam.information = information
    }

    override fun getMeetingTeamInformation(userUUID: UUID): MeetingTeamInformationGetResponse {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()

        val meetingTeam = user.team
        val userList = meetingTeam?.users ?: throw MeetingTeamNotFoundException()

        val information = meetingTeam.information ?: throw InformationNotFoundException()

        return meetingServiceUtils.toMeetingTeamInformationGetResponse(
            user.gender,
            TeamType.TRIPLE,
            userList,
            information,
            meetingTeam.name
        )
    }

    @Transactional
    override fun deleteMeetingTeam(userUUID: UUID) {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()

        val meetingTeam = user.team ?: throw MeetingTeamNotFoundException()

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
