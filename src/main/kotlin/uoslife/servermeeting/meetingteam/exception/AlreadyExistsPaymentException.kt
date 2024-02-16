package uoslife.servermeeting.meetingteam.exception

import uoslife.servermeeting.global.error.exception.BusinessException
import uoslife.servermeeting.global.error.exception.ErrorCode

class AlreadyExistsPaymentException : BusinessException(ErrorCode.AlREADY_EXISTS_PAYMENT)
