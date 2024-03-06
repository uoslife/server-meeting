package com.uoslife.core.auth.exception

import uoslife.servermeeting.global.error.exception.AccessDeniedException
import uoslife.servermeeting.global.error.exception.ErrorCode

class InvalidTokenException : AccessDeniedException(ErrorCode.INVALID_TOKEN)
