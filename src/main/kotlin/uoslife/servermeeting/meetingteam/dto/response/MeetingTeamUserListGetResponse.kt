package uoslife.servermeeting.meetingteam.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class MeetingTeamLeaderNameResponse(
    @Schema(description = "리더 이름", example = "석우진 (Join에선 NULL)") val leaderName: String? = null,
)
