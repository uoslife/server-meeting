package uoslife.servermeeting.meetingteam.exception

import uoslife.servermeeting.global.error.exception.ErrorCode
import uoslife.servermeeting.global.error.exception.InvalidValueException

class TeamCodeInvalidFormatException : InvalidValueException(ErrorCode.TEAM_CODE_INVALID_FORMAT)
