package uoslife.servermeeting.match.dto.response

import io.swagger.v3.oas.annotations.media.Schema

class MatchInformationResponse(
    @Schema(description = "매칭된 상대 정보")
    val opponnentUserInformation: MatchedMeetingTeamInformationGetResponse
)
