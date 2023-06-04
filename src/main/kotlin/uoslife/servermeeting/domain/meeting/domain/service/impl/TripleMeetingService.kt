package uoslife.servermeeting.domain.meeting.domain.service.impl

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import uoslife.servermeeting.domain.meeting.application.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.domain.meeting.application.response.MeetingTeamUser
import uoslife.servermeeting.domain.meeting.application.response.MeetingTeamUserListGetResponse
import uoslife.servermeeting.domain.meeting.application.response.UserProfile
import uoslife.servermeeting.domain.meeting.domain.dao.InformationUpdateDao
import uoslife.servermeeting.domain.meeting.domain.dao.PreferenceUpdateDao
import uoslife.servermeeting.domain.meeting.domain.dao.UserTeamDao
import uoslife.servermeeting.domain.meeting.domain.entity.Information
import uoslife.servermeeting.domain.meeting.domain.entity.MeetingTeam
import uoslife.servermeeting.domain.meeting.domain.entity.Preference
import uoslife.servermeeting.domain.meeting.domain.entity.enums.TeamType
import uoslife.servermeeting.domain.meeting.domain.exception.*
import uoslife.servermeeting.domain.meeting.domain.repository.InformationRepository
import uoslife.servermeeting.domain.meeting.domain.repository.MeetingTeamRepository
import uoslife.servermeeting.domain.meeting.domain.repository.PreferenceRepository
import uoslife.servermeeting.domain.meeting.domain.service.BaseMeetingService
import uoslife.servermeeting.domain.meeting.domain.util.UniqueCodeGenerator
import uoslife.servermeeting.domain.meeting.domain.util.Validator
import uoslife.servermeeting.domain.user.domain.entity.User
import uoslife.servermeeting.domain.user.domain.entity.enums.GenderType
import uoslife.servermeeting.domain.user.domain.exception.UserNotFoundException
import uoslife.servermeeting.domain.user.domain.repository.UserRepository
import java.time.LocalDate
import java.util.*

@Service
@Qualifier("tripleMeetingService")
class TripleMeetingService(
    private val userRepository: UserRepository,
    private val meetingTeamRepository: MeetingTeamRepository,
    private val informationRepository: InformationRepository,
    private val preferenceRepository: PreferenceRepository,
    private val userTeamDao: UserTeamDao,
    private val preferenceUpdateDao: PreferenceUpdateDao,
    private val informationUpdateDao: InformationUpdateDao,
    private val uniqueCodeGenerator: UniqueCodeGenerator,
    private val validator: Validator,
    @Value("\${app.season}")
    private val season: Int,
) : BaseMeetingService {
    override fun createMeetingTeam(userUUID: UUID, name: String?): String? {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()

        validator.isUserAlreadyHaveTeam(user)
        isTeamNameLeast2Character(name)

        val code = uniqueCodeGenerator.getUniqueTeamCode()

        val meetingTeam = meetingTeamRepository.save(
            MeetingTeam(
                season = season,
                name = name,
                code = code,
            ),
        )

        userTeamDao.saveUserTeam(meetingTeam, user, true, TeamType.TRIPLE)
        return code
    }

    override fun joinMeetingTeam(userUUID: UUID, code: String, isJoin: Boolean): MeetingTeamUserListGetResponse? {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()

        isTeamCodeValid(code)
        validator.isUserAlreadyHaveTeam(user)

        val meetingTeam = meetingTeamRepository.findByCode(code) ?: throw MeetingTeamNotFoundException()
        val leaderUserTeam = userTeamDao.findByTeamAndisLeader(meetingTeam, true)
            ?: throw TeamLeaderNotFoundException()
        isTeamFull(meetingTeam)
        isUserSameGenderWithTeamLeader(user, leaderUserTeam.user!!)

        return if (isJoin) {
            userTeamDao.saveUserTeam(meetingTeam, user, false, TeamType.TRIPLE)
            null
        } else {
            val userList = userTeamDao.findByTeam(meetingTeam).map { it.user!! }
            toMeetingTeamUserListGetResponse(meetingTeam.name!!, userList)
        }
    }

    override fun getMeetingTeamUserList(userUUID: UUID, code: String): MeetingTeamUserListGetResponse {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()

        isTeamCodeValid(code)

        val meetingTeam = meetingTeamRepository.findByCode(code) ?: throw MeetingTeamNotFoundException()
        isUserInTeam(user, meetingTeam)

        val userList = userTeamDao.findByTeam(meetingTeam).map { it.user!! }
        return toMeetingTeamUserListGetResponse(meetingTeam.name!!, userList)
    }

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

        informationUpSert(information, meetingTeam, informationDistance, informationFilter, informationMeetingTime)
        preferenceUpSert(preference, meetingTeam, preferenceDistance, preferenceFilter)
    }

    override fun getMeetingTeamInformation(userUUID: UUID): MeetingTeamInformationGetResponse {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()

        val userTeam = userTeamDao.findByUserWithMeetingTeam(user, TeamType.TRIPLE) ?: throw UserTeamNotFoundException()
        val meetingTeam = userTeam.team
        val userList = userTeamDao.findByTeam(meetingTeam).map { it.user!! }

        val information = informationRepository.findByMeetingTeam(meetingTeam) ?: throw InformationNotFoundException()
        val preference = preferenceRepository.findByMeetingTeam(meetingTeam) ?: throw PreferenceNotFoundException()

        return toMeetingTeamInformationGetResponse(user.gender, userList, information, preference, meetingTeam.name!!)
    }

    override fun deleteMeetingTeam(userUUID: UUID) {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()

        val userTeam = userTeamDao.findByUserWithMeetingTeam(user, TeamType.TRIPLE) ?: throw UserTeamNotFoundException()
        val meetingTeam = userTeam.team

        meetingTeamRepository.deleteById(meetingTeam.id!!)
    }


    private fun isTeamNameLeast2Character(name: String?) {
        println(name)
        if (name == null || name.length < 2) {
            throw TeamNameLeast2CharacterException()
        }
    }

    private fun isTeamCodeValid(code: String) {
        if (code.length != 4 || !code.matches(Regex("[A-Z0-9]{4}"))) {
            throw TeamCodeInvalidFormatException()
        }
    }

    private fun isTeamFull(team: MeetingTeam) {
        if (userTeamDao.countByTeam(team) >= 3) {
            throw TeamFullException()
        }
    }

    private fun isUserInTeam(user: User, team: MeetingTeam) {
        if (userTeamDao.findByUserAndTeam(user, team) == null) {
            throw UserNotInTeamException()
        }
    }

    private fun isUserSameGenderWithTeamLeader(user: User, teamLeaderUser: User) {
        if (user.gender != teamLeaderUser.gender) {
            throw TeamConsistOfSameGenderException()
        }
    }

    private fun toMeetingTeamUserListGetResponse(teamName: String, userList: List<User>):
        MeetingTeamUserListGetResponse {
        val currentYear: Int = LocalDate.now().year

        return MeetingTeamUserListGetResponse(
            teamName = teamName,
            userList.map {
                MeetingTeamUser(
                    nickname = it.nickname,
                    age = currentYear - it.birthYear!! + 1,
                )
            },
        )
    }

    // information 이 없으면 생성, 있으면 수정
    private fun informationUpSert(
        information: Information?,
        meetingTeam: MeetingTeam,
        informationDistance: String,
        informationFilter: String,
        informationMeetingTime: String,
    ) {
        if (information == null) {
            informationRepository.save(
                Information(
                    meetingTeam = meetingTeam,
                    filterInfo = informationFilter,
                    distanceInfo = informationDistance,
                    meetingTime = informationMeetingTime,
                ),
            )
        } else {
            informationUpdateDao.updateInformationByMeetingTeam(
                meetingTeam,
                informationDistance,
                informationFilter,
                informationMeetingTime,
            )
        }
    }

    // preference 가 없으면 생성, 있으면 수정
    private fun preferenceUpSert(
        preference: Preference?,
        meetingTeam: MeetingTeam,
        preferenceDistance: String,
        preferenceFilter: String,
    ) {
        if (preference == null) {
            preferenceRepository.save(
                Preference(
                    meetingTeam = meetingTeam,
                    filterCondition = preferenceFilter,
                    distanceCondition = preferenceDistance,
                ),
            )
        } else {
            preferenceUpdateDao.updatePreferenceByMeetingTeam(
                meetingTeam,
                preferenceFilter,
                preferenceDistance,
            )
        }
    }

    fun toMeetingTeamInformationGetResponse(
        gender: GenderType,
        userList: List<User>,
        information: Information,
        preference: Preference,
        teamName: String,
    ): MeetingTeamInformationGetResponse {
        val currentYear: Int = LocalDate.now().year

        return MeetingTeamInformationGetResponse(
            teamType = TeamType.TRIPLE,
            teamName = teamName,
            sex = gender,
            informationDistance = information.distanceInfo,
            informationFilter = information.filterInfo,
            informationMeetingTime = information.meetingTime,
            preferenceDistance = preference.distanceCondition,
            preferenceFilter = preference.filterCondition,
            teamUserList = userList.map {
                UserProfile(
                    nickname = it.nickname,
                    age = currentYear - it.birthYear!! + 1,
                    kakaoTalkId = it.kakaoTalkId!!,
                    department = it.department!!,
                    studentType = it.studentType!!,
                    height = it.height,
                    smoking = it.smoking,
                    spiritAnimal = it.spiritAnimal,
                    MBTI = it.mbti!!,
                    interest = it.interest,
                )
            },
        )
    }
}
