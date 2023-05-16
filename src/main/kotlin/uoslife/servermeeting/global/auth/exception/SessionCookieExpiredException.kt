package uoslife.servermeeting.global.auth.exception

import uoslife.servermeeting.global.error.exception.AccessDeniedException
import uoslife.servermeeting.global.error.exception.ErrorCode

class SessionCookieExpiredException : AccessDeniedException(ErrorCode.SESSION_COOKIE_EXPIRED)
