package uoslife.servermeeting.meetingteam.dto.vo

import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamUser
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamUserListGetResponse
import uoslife.servermeeting.meetingteam.exception.UserInfoNotCompletedException
import uoslife.servermeeting.user.entity.User

class MeetingTeamUsers(private val users: List<User>) {

    fun toMeetingTeamUserListGetResponse(teamName: String): MeetingTeamUserListGetResponse {
        return MeetingTeamUserListGetResponse(
            teamName = teamName,
            users.map {
                it.userInformation ?: throw UserInfoNotCompletedException()
                MeetingTeamUser(
                    name = it.name,
                    department = it.userInformation!!.department,
                    interest = it.userInformation!!.interest,
                    kakaoTalkId = it.kakaoTalkId,
                    studentNumber = it.userInformation!!.studentNumber
                )
            },
        )
    }
}
