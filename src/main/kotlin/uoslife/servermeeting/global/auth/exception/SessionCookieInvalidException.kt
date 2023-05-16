package uoslife.servermeeting.global.auth.exception

import uoslife.servermeeting.global.error.exception.AccessDeniedException
import uoslife.servermeeting.global.error.exception.ErrorCode

class SessionCookieInvalidException : AccessDeniedException(ErrorCode.SESSION_COOKIE_INVALID)
