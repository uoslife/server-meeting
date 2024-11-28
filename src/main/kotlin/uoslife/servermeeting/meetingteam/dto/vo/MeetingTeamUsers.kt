package uoslife.servermeeting.meetingteam.dto.vo

import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamUser
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamUserListGetResponse
import uoslife.servermeeting.meetingteam.entity.UserTeam
import uoslife.servermeeting.meetingteam.exception.UserInfoNotCompletedException

class MeetingTeamUsers(private val userTeams: List<UserTeam>) {

    fun toMeetingTeamUserListGetResponse(teamName: String?): MeetingTeamUserListGetResponse {
        return MeetingTeamUserListGetResponse(
            teamName = teamName,
            userTeams.map {
                it.user.userInformation ?: throw UserInfoNotCompletedException()
                MeetingTeamUser(
                    isLeader = it.isLeader,
                    name = it.user.name,
                    department = it.user.userInformation!!.department,
                    interest = it.user.userInformation!!.interest,
                    kakaoTalkId = it.user.kakaoTalkId,
                    studentNumber = it.user.userInformation!!.studentNumber
                )
            },
        )
    }
}
