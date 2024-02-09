package uoslife.servermeeting.domain.meeting.domain.vo

import java.time.LocalDate
import uoslife.servermeeting.domain.meeting.application.response.MeetingTeamUser
import uoslife.servermeeting.domain.meeting.application.response.MeetingTeamUserListGetResponse
import uoslife.servermeeting.domain.user.domain.entity.User

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
