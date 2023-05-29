package uoslife.servermeeting.domain.meeting.domain.service.impl

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import uoslife.servermeeting.domain.meeting.application.response.MeetingTeamInformationGetResponse
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
import uoslife.servermeeting.domain.user.domain.entity.User
import uoslife.servermeeting.domain.user.domain.exception.UserNotFoundException
import uoslife.servermeeting.domain.user.domain.repository.UserRepository
import java.time.LocalDate
import java.util.*

@Service
@Qualifier("singleMeetingService")
class SingleMeetingService(
    private val userRepository: UserRepository,
    private val meetingTeamRepository: MeetingTeamRepository,
    private val informationRepository: InformationRepository,
    private val preferenceRepository: PreferenceRepository,
    private val userTeamDao: UserTeamDao,
    private val preferenceUpdateDao: PreferenceUpdateDao,
    private val informationUpdateDao: InformationUpdateDao,
    @Value("\${app.season}")
    private val season: Int,
) : BaseMeetingService {
    override fun createMeetingTeam(userUUID: UUID, name: String?): String? {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()

        if (userTeamDao.findByUser(user) != null) {
            throw UserAlreadyHaveTeamException()
        }

        val meetingTeam = meetingTeamRepository.save(
            MeetingTeam(
                season = season,
                code = "",
            ),
        )

        userTeamDao.saveUserTeam(meetingTeam, user, true, TeamType.SINGLE)
        return ""
    }

    override fun joinMeetingTeam(userUUID: UUID, code: String, isJoin: Boolean): MeetingTeamUserListGetResponse? {
        throw InSingleMeetingTeamNoJoinTeamException()
    }

    override fun getMeetingTeamUserList(userUUID: UUID, code: String): MeetingTeamUserListGetResponse {
        throw InSingleMeetingTeamOnlyOneUserException()
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

        val userTeam = userTeamDao.findByUserWithMeetingTeam(user, TeamType.SINGLE) ?: throw UserTeamNotFoundException()
        val meetingTeam =
            meetingTeamRepository.findByIdOrNull(userTeam.team.id!!) ?: throw MeetingTeamNotFoundException()

        // information and preference 는 하나만 존재해야 함 중복 체크
        val information = informationRepository.findByMeetingTeam(meetingTeam)
        val preference = preferenceRepository.findByMeetingTeam(meetingTeam)

        informationUpSert(information, meetingTeam, informationDistance, informationFilter, informationMeetingTime)
        preferenceUpSert(preference, meetingTeam, preferenceDistance, preferenceFilter)
    }

    override fun getMeetingTeamInformation(userUUID: UUID): MeetingTeamInformationGetResponse {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()

        val userTeam = userTeamDao.findByUserWithMeetingTeam(user, TeamType.SINGLE) ?: throw UserTeamNotFoundException()
        val meetingTeam =
            meetingTeamRepository.findByIdOrNull(userTeam.team.id!!) ?: throw MeetingTeamNotFoundException()

        val information = informationRepository.findByMeetingTeam(meetingTeam) ?: throw InformationNotFoundException()
        val preference = preferenceRepository.findByMeetingTeam(meetingTeam) ?: throw PreferenceNotFoundException()

        return toMeetingTeamInformationGetResponse(user, information, preference)
    }

    override fun deleteMeetingTeam(userUUID: UUID) {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()

        val userTeam = userTeamDao.findByUserWithMeetingTeam(user, TeamType.SINGLE) ?: throw UserTeamNotFoundException()
        val meetingTeam =
            meetingTeamRepository.findByIdOrNull(userTeam.team.id!!) ?: throw MeetingTeamNotFoundException()

        meetingTeamRepository.deleteById(meetingTeam.id!!)
    }

    private fun toMeetingTeamInformationGetResponse(
        user: User,
        information: Information,
        preference: Preference,
    ): MeetingTeamInformationGetResponse {
        val currentYear: Int = LocalDate.now().year
        val userBirthYear: Int = user.birthYear as Int
        return MeetingTeamInformationGetResponse(
            sex = user.gender,
            teamType = TeamType.SINGLE,
            teamName = null,
            informationDistance = information.distanceInfo,
            informationFilter = information.filterInfo,
            informationMeetingTime = information.meetingTime,
            preferenceDistance = preference.distanceCondition,
            preferenceFilter = preference.filterCondition,
            teamUserList = listOf(
                UserProfile(
                    nickname = user.nickname,
                    age = currentYear - userBirthYear + 1,
                    kakaoTalkId = user.kakaoTalkId!!,
                    department = user.department!!,
                    studentType = user.studentType!!,
                    height = user.height,
                    smoking = user.smoking,
                    spiritAnimal = user.spiritAnimal,
                    MBTI = user.mbti!!,
                    interest = user.interest,
                ),
            ),
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
}
