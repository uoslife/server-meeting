package uoslife.servermeeting.meetingteam.dto.vo

import uoslife.servermeeting.meetingteam.entity.UserTeam
import uoslife.servermeeting.meetingteam.exception.UserInfoNotCompletedException

class MeetingTeamUsers(private val userTeams: List<UserTeam>) {
    fun getLeaderName(): String? {
        return userTeams.first { it.isLeader }.user.name ?: throw UserInfoNotCompletedException()
    }
}
