package uoslife.servermeeting.match.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamInformationGetResponse

class MatchInformationResponse(
    @Schema(description = "본인 이름", example = "호랑이") val myName: String,
    @Schema(description = "매칭된 상대 정보")
    val opponnentUserInformation: MeetingTeamInformationGetResponse
)
