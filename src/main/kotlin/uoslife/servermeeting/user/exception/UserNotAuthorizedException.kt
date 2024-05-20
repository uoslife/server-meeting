package uoslife.servermeeting.user.exception

import uoslife.servermeeting.global.error.exception.BusinessException
import uoslife.servermeeting.global.error.exception.ErrorCode

class UserNotAuthorizedException : BusinessException(ErrorCode.USER_NOT_AUTHORIZED)
