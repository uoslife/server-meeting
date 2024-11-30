package uoslife.servermeeting.global.aop

import uoslife.servermeeting.global.error.exception.AccessDeniedException
import uoslife.servermeeting.global.error.exception.ErrorCode

class DuplicateRequestException : AccessDeniedException(ErrorCode.DUPLICATE_REQUEST)
