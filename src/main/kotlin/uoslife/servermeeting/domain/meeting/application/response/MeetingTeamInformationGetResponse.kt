package uoslife.servermeeting.domain.meeting.application.response

import uoslife.servermeeting.domain.meeting.domain.entity.enums.TeamType
import uoslife.servermeeting.domain.user.domain.entity.enums.DepartmentNameType
import uoslife.servermeeting.domain.user.domain.entity.enums.GenderType
import uoslife.servermeeting.domain.user.domain.entity.enums.StudentType

data class MeetingTeamInformationGetResponse(
    val teamType: TeamType,
    val sex: GenderType,
    val teamUserList: List<UserProfile>,
    val informationFilter: String,
    val informationDistance: String,
    val informationMeetingTime: String,
    val preferenceFilter: String,
    val preferenceDistance: String,
)

data class UserProfile(
    val nickname: String,
    val age: Int,
    val kakaoTalkId: String,
    val department: DepartmentNameType,
    val studentType: StudentType,
    val height: Int?,
    val smoking: Boolean?,
    val spiritAnimal: String?,
    val MBTI: String?,
    val interest: String?,
)
