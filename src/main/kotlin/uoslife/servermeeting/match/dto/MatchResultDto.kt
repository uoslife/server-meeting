package uoslife.servermeeting.match.dto

import uoslife.servermeeting.meetingteam.entity.enums.TeamType

data class MatchResultDto(val teamType: TeamType, val matchId: Long?)
