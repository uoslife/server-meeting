package uoslife.servermeeting.meetingteam.exception

import uoslife.servermeeting.global.error.exception.BusinessException
import uoslife.servermeeting.global.error.exception.ErrorCode

class PreconditionFailedException : BusinessException(ErrorCode.PRECONDITION_FAILED)
