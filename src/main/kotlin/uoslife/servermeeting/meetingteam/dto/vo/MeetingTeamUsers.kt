package uoslife.servermeeting.meetingteam.dto.vo

import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamUser
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamUserListGetResponse
import uoslife.servermeeting.user.entity.User

class MeetingTeamUsers(private val users: List<User>) {

    fun toMeetingTeamUserListGetResponse(teamName: String): MeetingTeamUserListGetResponse {
        return MeetingTeamUserListGetResponse(
            teamName = teamName,
            users.map {
                MeetingTeamUser(
                    name = it.name,
                )
            },
        )
    }
}
