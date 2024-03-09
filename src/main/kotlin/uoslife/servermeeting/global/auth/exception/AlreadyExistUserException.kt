package uoslife.servermeeting.global.auth.exception

import uoslife.servermeeting.global.error.exception.AccessDeniedException
import uoslife.servermeeting.global.error.exception.ErrorCode

class AlreadyExistUserException : AccessDeniedException(ErrorCode.USER_ALREADY_EXISTING)
