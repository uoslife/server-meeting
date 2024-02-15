package uoslife.servermeeting.meetingteam.exception

import uoslife.servermeeting.global.error.exception.BusinessException
import uoslife.servermeeting.global.error.exception.ErrorCode

class UserAlreadyHaveTeamException : BusinessException(ErrorCode.USER_ALREADY_HAVE_TEAM)
