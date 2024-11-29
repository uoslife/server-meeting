package uoslife.servermeeting.match.exception

import uoslife.servermeeting.global.error.exception.AccessDeniedException
import uoslife.servermeeting.global.error.exception.ErrorCode

class UnauthorizedMatchAccessException : AccessDeniedException(ErrorCode.UNAUTHORIZED_MATCH_ACCESS)
