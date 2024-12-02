package uoslife.servermeeting.match.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import uoslife.servermeeting.meetingteam.entity.enums.TeamType

data class MatchResultResponse(
    @Schema(description = "신청한 미팅의 종류") val matchType: TeamType,
    @Schema(description = "매칭 성공 여부") val isMatched: Boolean,
    @Schema(description = "매치 ID", nullable = true) val matchId: Long?,
)
