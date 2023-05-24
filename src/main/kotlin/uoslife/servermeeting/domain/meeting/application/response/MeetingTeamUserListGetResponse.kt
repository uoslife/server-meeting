package uoslife.servermeeting.domain.meeting.application.response

data class MeetingTeamUserListGetResponse(
    val teamLeaderNickname: String? = null,
    val teamMate1Nickname: String? = null,
    val teamMate2Nickname: String? = null,
)
