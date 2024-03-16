package uoslife.servermeeting.user.exception

import uoslife.servermeeting.global.error.exception.BusinessException
import uoslife.servermeeting.global.error.exception.ErrorCode

class UserAlreadyExistsException : BusinessException(ErrorCode.USER_ALREADY_EXISTING)
