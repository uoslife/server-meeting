package uoslife.servermeeting.match.dto.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import uoslife.servermeeting.meetingteam.entity.enums.TeamType

data class MatchResultResponse
@JsonCreator
constructor(
    @Schema(description = "신청한 미팅의 종류") @JsonProperty("matchType") val matchType: TeamType,
    @Schema(description = "매칭 성공 여부") @JsonProperty("matched") val isMatched: Boolean,
    @Schema(description = "매치 ID", nullable = true) @JsonProperty("matchId") val matchId: Long?,
)
