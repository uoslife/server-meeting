package uoslife.servermeeting.meetingteam.exception

import uoslife.servermeeting.global.error.exception.AccessDeniedException
import uoslife.servermeeting.global.error.exception.ErrorCode

class UserNotInTeamException : AccessDeniedException(ErrorCode.USER_NOT_IN_TEAM)
