package uoslife.servermeeting.meetingteam.dto.response

import uoslife.servermeeting.meetingteam.entity.Information
import uoslife.servermeeting.meetingteam.entity.Preference
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.user.entity.enums.*
import uoslife.servermeeting.verification.dto.University

data class MeetingTeamInformationGetResponse(
    val teamType: TeamType,
    val teamName: String?,
    val sex: GenderType,
    val teamUserList: List<UserProfile>,
    val information: Information,
    val preference: Preference,
)

data class UserProfile(
    val name: String?,
    val age: Int?,
    val height: Int?,
    val university: University?,
    val department: String?,
    val studentType: StudentType?,
    val kakaoTalkId: String?,
    val smoking: SmokingType?,
    val religion: ReligionType?,
    val drinkingMin: Int?,
    val drinkingMax: Int?,
    val spiritAnimal: List<SpiritAnimalType>?,
    val mbti: String?,
    val interest: List<InterestType>?,
)
