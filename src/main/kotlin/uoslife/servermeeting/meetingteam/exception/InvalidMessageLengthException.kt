package uoslife.servermeeting.meetingteam.exception

import uoslife.servermeeting.global.error.exception.BusinessException
import uoslife.servermeeting.global.error.exception.ErrorCode

class InvalidMessageLengthException : BusinessException(ErrorCode.INVALID_MESSAGE_LENGTH)
