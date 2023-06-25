package uoslife.servermeeting.domain.meeting.domain.service.impl

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.domain.meeting.application.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.domain.meeting.application.response.MeetingTeamUserListGetResponse
import uoslife.servermeeting.domain.meeting.domain.dao.UserTeamDao
import uoslife.servermeeting.domain.meeting.domain.entity.MeetingTeam
import uoslife.servermeeting.domain.meeting.domain.entity.UserTeam
import uoslife.servermeeting.domain.meeting.domain.entity.enums.TeamType
import uoslife.servermeeting.domain.meeting.domain.exception.*
import uoslife.servermeeting.domain.meeting.domain.repository.InformationRepository
import uoslife.servermeeting.domain.meeting.domain.repository.MeetingTeamRepository
import uoslife.servermeeting.domain.meeting.domain.repository.PreferenceRepository
import uoslife.servermeeting.domain.meeting.domain.service.BaseMeetingService
import uoslife.servermeeting.domain.meeting.domain.service.util.MeetingServiceUtils
import uoslife.servermeeting.domain.meeting.domain.util.UniqueCodeGenerator
import uoslife.servermeeting.domain.meeting.domain.util.Validator
import uoslife.servermeeting.domain.meeting.domain.vo.MeetingTeamUsers
import uoslife.servermeeting.domain.user.domain.exception.UserNotFoundException
import uoslife.servermeeting.domain.user.domain.repository.UserRepository
import java.util.*

@Service
@Transactional(readOnly = true)
@Qualifier("tripleMeetingService")
class TripleMeetingService(
    private val userRepository: UserRepository,
    private val meetingTeamRepository: MeetingTeamRepository,
    private val informationRepository: InformationRepository,
    private val preferenceRepository: PreferenceRepository,
    private val userTeamDao: UserTeamDao,
    private val uniqueCodeGenerator: UniqueCodeGenerator,
    private val validator: Validator,
    private val meetingServiceUtils: MeetingServiceUtils,
    @Value("\${app.season}")
    private val season: Int,
) : BaseMeetingService {

    @Transactional
    override fun createMeetingTeam(userUUID: UUID, name: String?): String? {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()

        validator.isUserAlreadyHaveTeam(user)
        validator.isTeamNameLeast2Character(name)

        val code = uniqueCodeGenerator.getUniqueTeamCode()
        val meetingTeam = saveMeetingTeam(name, code)

        val newUserTeam = UserTeam.createUserTeam(meetingTeam, user, true, TeamType.TRIPLE)
        userTeamDao.saveUserTeam(newUserTeam)
        return code
    }

    @Transactional
    override fun joinMeetingTeam(userUUID: UUID, code: String, isJoin: Boolean): MeetingTeamUserListGetResponse? {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()

        validator.isTeamCodeValid(code)
        validator.isUserAlreadyHaveTeam(user)

        val meetingTeam = meetingTeamRepository.findByCode(code) ?: throw MeetingTeamNotFoundException()
        val leaderUserTeam = userTeamDao.findByTeamAndisLeader(meetingTeam, true)
            ?: throw TeamLeaderNotFoundException()

        validator.isTeamFull(meetingTeam)
        validator.isUserSameGenderWithTeamLeader(user, leaderUserTeam.user!!)

        return if (isJoin) {
            val newUserTeam = UserTeam.createUserTeam(meetingTeam, user, false, TeamType.TRIPLE)
            userTeamDao.saveUserTeam(newUserTeam)
            null
        } else {
            val meetingTeamUsers = MeetingTeamUsers(userTeamDao.findByTeam(meetingTeam).map { it.user!! })
            meetingTeamUsers.toMeetingTeamUserListGetResponse(meetingTeam.name!!)
        }
    }

    override fun getMeetingTeamUserList(userUUID: UUID, code: String): MeetingTeamUserListGetResponse {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()
        validator.isTeamCodeValid(code)

        val meetingTeam = meetingTeamRepository.findByCode(code) ?: throw MeetingTeamNotFoundException()
        validator.isUserInTeam(user, meetingTeam)

        val meetingTeamUsers = MeetingTeamUsers(userTeamDao.findByTeam(meetingTeam).map { it.user!! })
        return meetingTeamUsers.toMeetingTeamUserListGetResponse(meetingTeam.name!!)
    }

    @Transactional
    override fun updateMeetingTeamInformation(
        userUUID: UUID,
        informationDistance: String,
        informationFilter: String,
        informationMeetingTime: String,
        preferenceDistance: String,
        preferenceFilter: String,
    ) {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()

        val userTeam = userTeamDao.findByUserWithMeetingTeam(user, TeamType.TRIPLE) ?: throw UserTeamNotFoundException()
        val meetingTeam = userTeam.team

        // information and preference 는 하나만 존재해야 함 중복 체크
        val information = informationRepository.findByMeetingTeam(meetingTeam)
        val preference = preferenceRepository.findByMeetingTeam(meetingTeam)

        meetingServiceUtils
            .informationUpSert(information, meetingTeam, informationDistance, informationFilter, informationMeetingTime)
        meetingServiceUtils.preferenceUpSert(preference, meetingTeam, preferenceDistance, preferenceFilter)
    }

    override fun getMeetingTeamInformation(userUUID: UUID): MeetingTeamInformationGetResponse {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()

        val userTeam = userTeamDao.findByUserWithMeetingTeam(user, TeamType.TRIPLE) ?: throw UserTeamNotFoundException()
        val meetingTeam = userTeam.team
        val userList = userTeamDao.findByTeam(meetingTeam).map { it.user!! }

        val information = informationRepository.findByMeetingTeam(meetingTeam) ?: throw InformationNotFoundException()
        val preference = preferenceRepository.findByMeetingTeam(meetingTeam) ?: throw PreferenceNotFoundException()

        return meetingServiceUtils
            .toMeetingTeamInformationGetResponse(
                user.gender, TeamType.TRIPLE,
                userList, information, preference, meetingTeam.name
            )
    }

    @Transactional
    override fun deleteMeetingTeam(userUUID: UUID) {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()

        val userTeam = userTeamDao.findByUserWithMeetingTeam(user, TeamType.TRIPLE) ?: throw UserTeamNotFoundException()
        val meetingTeam = userTeam.team

        meetingTeamRepository.deleteById(meetingTeam.id!!)
    }

    @Transactional
    fun saveMeetingTeam(name: String?, code: String): MeetingTeam {
        return meetingTeamRepository.save(
            MeetingTeam(
                season = season,
                name = name,
                code = code,
            ),
        )
    }
}
