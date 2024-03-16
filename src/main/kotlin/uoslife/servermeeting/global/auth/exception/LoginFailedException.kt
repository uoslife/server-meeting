package uoslife.servermeeting.global.auth.exception

import uoslife.servermeeting.global.error.exception.BusinessException
import uoslife.servermeeting.global.error.exception.ErrorCode

class LoginFailedException : BusinessException(ErrorCode.LOG_IN_FAILED)
