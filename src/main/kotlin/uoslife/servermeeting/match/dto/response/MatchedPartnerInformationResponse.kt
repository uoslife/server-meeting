package uoslife.servermeeting.match.dto.response

import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.meetingteam.dto.response.UserCardProfile
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.user.entity.enums.GenderType

data class MatchedPartnerInformationResponse(
    val teamType: TeamType,
    val teamName: String?,
    val course: String?,
    val gender: GenderType,
    val code: String?,
    val meetingTeamUserProfiles: List<UserCardProfile>?
)

object MeetingDtoConverter {
    fun toMatchedPartnerInformationResponse(
        response: MeetingTeamInformationGetResponse
    ): MatchedPartnerInformationResponse {
        return MatchedPartnerInformationResponse(
            teamType = response.teamType,
            teamName = response.teamName,
            course = response.course,
            gender = response.gender,
            code = response.code,
            meetingTeamUserProfiles = response.meetingTeamUserProfiles
        )
    }
}
