package uoslife.servermeeting.domain.meeting.application.response

data class MeetingTeamUserListGetResponse(
    val teamName: String,
    val userList: List<MeetingTeamUser>,
)

data class MeetingTeamUser(
    val nickname: String? = null,
    val age: Int? = null,
)
