package uoslife.servermeeting.global.auth.exception

import uoslife.servermeeting.global.error.exception.AccessDeniedException
import uoslife.servermeeting.global.error.exception.ErrorCode

class UserNotFoundException : AccessDeniedException(ErrorCode.USER_NOT_FOUND)
