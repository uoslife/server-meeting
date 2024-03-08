package uoslife.servermeeting.meetingteam.exception

import uoslife.servermeeting.global.error.exception.ErrorCode
import uoslife.servermeeting.global.error.exception.InvalidValueException

class InvalidTeamNameException : InvalidValueException(ErrorCode.INVALID_TEAM_NAME)
