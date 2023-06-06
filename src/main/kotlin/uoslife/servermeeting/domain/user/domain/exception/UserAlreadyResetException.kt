package uoslife.servermeeting.domain.user.domain.exception

import uoslife.servermeeting.global.error.exception.BusinessException
import uoslife.servermeeting.global.error.exception.ErrorCode

class UserAlreadyResetException : BusinessException(ErrorCode.USER_ALREADY_RESET)
