package uoslife.servermeeting.domain.meeting.application.response

data class MeetingTeamUserListGetResponse(
    val teamLeader: MeetingTeamUser? = null,
    val teamMate1: MeetingTeamUser? = null,
    val teamMate2: MeetingTeamUser? = null,
)

data class MeetingTeamUser(
    val nickname: String? = null,
    val age: Int? = null,
)
