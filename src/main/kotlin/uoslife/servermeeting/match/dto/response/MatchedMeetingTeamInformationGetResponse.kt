package uoslife.servermeeting.match.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import uoslife.servermeeting.meetingteam.dto.response.UserProfile
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.user.entity.enums.GenderType

data class MatchedMeetingTeamInformationGetResponse(
    @field:NotNull @Schema(description = "팀 타입", example = "SINGLE") val teamType: TeamType,
    @Schema(description = "팀 이름", example = "팀 이름(1:1인 경우 null)") val teamName: String?,
    @field:NotNull @Schema(description = "성별", example = "MALE") val gender: GenderType,
    @Schema(description = "팀에 속한 유저 정보") val leaderProfile: UserProfile?,
    @Schema(description = "질문 응답값") val information: MatchedInformation?,
    @Schema(description = "상대에게 전하는 메세지") val message: String?
)

data class MatchedInformation(
    val gender: GenderType? = null,
    val questions: List<Int>? = null,
)
