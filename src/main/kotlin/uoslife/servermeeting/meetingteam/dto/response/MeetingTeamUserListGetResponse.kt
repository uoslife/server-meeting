package uoslife.servermeeting.meetingteam.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

data class MeetingTeamUserListGetResponse(
    @Schema(description = "팀 이름", example = "팀이름") @field:NotNull val teamName: String,
    @Schema(description = "팀에 속한 유저 리스트") val userList: List<MeetingTeamUser>,
)

data class MeetingTeamUser(
    val name: String? = null,
)
