package uoslife.servermeeting.meetingteam.dto.vo

import java.time.LocalDate
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamUser
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamUserListGetResponse
import uoslife.servermeeting.user.entity.User

class MeetingTeamUsers(private val users: List<User>) {

    fun toMeetingTeamUserListGetResponse(teamName: String): MeetingTeamUserListGetResponse {
        val currentYear: Int = LocalDate.now().year

        return MeetingTeamUserListGetResponse(
            teamName = teamName,
            users.map {
                MeetingTeamUser(
                    nickname = it.nickname,
                    age = currentYear,
                )
            },
        )
    }
}
