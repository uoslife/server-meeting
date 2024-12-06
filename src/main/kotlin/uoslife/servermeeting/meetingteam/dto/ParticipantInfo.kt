package uoslife.servermeeting.meetingteam.dto

import uoslife.servermeeting.meetingteam.entity.enums.TeamType

data class ParticipantInfo(val userId: Long, val teamType: TeamType)
