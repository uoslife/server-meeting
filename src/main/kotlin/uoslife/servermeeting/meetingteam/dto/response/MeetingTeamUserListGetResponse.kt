package uoslife.servermeeting.meetingteam.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class MeetingTeamUserListGetResponse(
    @Schema(description = "팀 이름", example = "팀이름") val teamName: String? = null,
    @Schema(description = "팀에 속한 유저 리스트") val userList: List<MeetingTeamUser>,
)

data class MeetingTeamUser(
    val isLeader: Boolean,
    val name: String? = null,
    val department: String?,
    val studentNumber: Int?,
    val interest: List<String>?,
    val kakaoTalkId: String?
)
