package uoslife.servermeeting.match.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import uoslife.servermeeting.meetingteam.dto.response.UserCardProfile
import uoslife.servermeeting.user.entity.enums.GenderType

data class MatchInfoResponse(
    @Schema(description = "매칭 성공 여부") val isMatched: Boolean,
    @Schema(description = "상대 팀 정보", nullable = true) val partnerTeam: PartnerTeamInfo?
) {
    data class PartnerTeamInfo(
        @Schema(description = "팀 이름 (3대3)") val teamName: String?,
        @Schema(description = "데이트 코스 (1대1)") val course: String?,
        @Schema(description = "성별") val gender: GenderType,
        @Schema(description = "팀원 프로필 목록") val userProfiles: List<UserCardProfile>
    )

    companion object {
        fun toMatchInfoResponse(
            response: MatchedMeetingTeamInformationGetResponse
        ): MatchInfoResponse {
            return MatchInfoResponse(
                isMatched = true,
                partnerTeam =
                    PartnerTeamInfo(
                        teamName = response.teamName,
                        course = response.course,
                        gender = response.gender,
                        userProfiles = response.userProfiles!!
                    )
            )
        }
    }
}
