package uoslife.servermeeting.meetingteam.exception

import uoslife.servermeeting.global.error.exception.EntityNotFoundException
import uoslife.servermeeting.global.error.exception.ErrorCode

class AccessTokenNotFoundException : EntityNotFoundException(ErrorCode.ACCESS_TOKEN_NOT_FOUND)
