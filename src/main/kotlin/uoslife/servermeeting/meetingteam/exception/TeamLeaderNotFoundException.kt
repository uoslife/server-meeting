package uoslife.servermeeting.meetingteam.exception

import uoslife.servermeeting.global.error.exception.EntityNotFoundException
import uoslife.servermeeting.global.error.exception.ErrorCode

class TeamLeaderNotFoundException : EntityNotFoundException(ErrorCode.TEAM_LEADER_NOT_FOUND)
