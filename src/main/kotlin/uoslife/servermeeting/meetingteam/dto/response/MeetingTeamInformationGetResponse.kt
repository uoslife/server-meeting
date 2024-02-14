package uoslife.servermeeting.meetingteam.dto.response

import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.user.entity.enums.DepartmentNameType
import uoslife.servermeeting.user.entity.enums.GenderType
import uoslife.servermeeting.user.entity.enums.StudentType

data class MeetingTeamInformationGetResponse(
    val teamType: TeamType,
    val teamName: String?,
    val sex: GenderType,
    val teamUserList: List<UserProfile>,
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
