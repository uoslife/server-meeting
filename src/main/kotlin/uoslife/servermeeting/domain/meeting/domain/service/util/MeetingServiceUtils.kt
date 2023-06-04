package uoslife.servermeeting.domain.meeting.domain.service.util

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.domain.meeting.application.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.domain.meeting.application.response.UserProfile
import uoslife.servermeeting.domain.meeting.domain.dao.InformationUpdateDao
import uoslife.servermeeting.domain.meeting.domain.dao.PreferenceUpdateDao
import uoslife.servermeeting.domain.meeting.domain.entity.Information
import uoslife.servermeeting.domain.meeting.domain.entity.MeetingTeam
import uoslife.servermeeting.domain.meeting.domain.entity.Preference
import uoslife.servermeeting.domain.meeting.domain.entity.enums.TeamType
import uoslife.servermeeting.domain.meeting.domain.repository.InformationRepository
import uoslife.servermeeting.domain.meeting.domain.repository.PreferenceRepository
import uoslife.servermeeting.domain.user.domain.entity.User
import uoslife.servermeeting.domain.user.domain.entity.enums.GenderType
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class MeetingServiceUtils(
    private val informationUpdateDao: InformationUpdateDao,
    private val informationRepository: InformationRepository,
    private val preferenceUpdateDao: PreferenceUpdateDao,
    private val preferenceRepository: PreferenceRepository,
) {

    // information 이 없으면 생성, 있으면 수정
    @Transactional
    fun informationUpSert(
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
    @Transactional
    fun preferenceUpSert(
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
        teamType: TeamType,
        userList: List<User>,
        information: Information,
        preference: Preference,
        teamName: String?,
    ): MeetingTeamInformationGetResponse {
        val currentYear: Int = LocalDate.now().year

        return MeetingTeamInformationGetResponse(
            teamType = teamType,
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
