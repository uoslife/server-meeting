package uoslife.servermeeting.global.auth.exception

import uoslife.servermeeting.global.error.exception.ErrorCode
import uoslife.servermeeting.global.error.exception.AccessDeniedException


class AlreadyExistUserException : AccessDeniedException(ErrorCode.USER_ALREADY_EXISTING)
