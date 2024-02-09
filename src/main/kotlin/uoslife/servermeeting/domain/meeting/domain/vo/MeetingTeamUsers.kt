package uoslife.servermeeting.domain.meeting.domain.vo

import uoslife.servermeeting.domain.meeting.application.response.MeetingTeamUser
import uoslife.servermeeting.domain.meeting.application.response.MeetingTeamUserListGetResponse
import uoslife.servermeeting.domain.user.domain.entity.User
import java.time.LocalDate

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
