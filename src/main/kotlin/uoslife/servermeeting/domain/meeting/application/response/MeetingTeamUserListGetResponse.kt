package uoslife.servermeeting.domain.meeting.application.response

data class MeetingTeamUserListGetResponse(
    val userList: List<MeetingTeamUser>,
)

data class MeetingTeamUser(
    val nickname: String? = null,
    val age: Int? = null,
)
