package uoslife.servermeeting.meetingteam.dto.response

data class MeetingTeamUserListGetResponse(
    val teamName: String,
    val userList: List<MeetingTeamUser>,
)

data class MeetingTeamUser(
    val name: String? = null,
)
