package uoslife.servermeeting.global.auth.exception

import uoslife.servermeeting.global.error.exception.AccessDeniedException
import uoslife.servermeeting.global.error.exception.ErrorCode

class SessionCookieNotFoundException : AccessDeniedException(ErrorCode.SESSION_COOKIE_NOT_FOUND_IN_HEADER)
