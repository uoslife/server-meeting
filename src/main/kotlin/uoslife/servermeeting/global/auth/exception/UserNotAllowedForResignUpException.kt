package com.uoslife.core.auth.exception

import uoslife.servermeeting.global.error.exception.AccessDeniedException
import uoslife.servermeeting.global.error.exception.ErrorCode

class UserNotAllowedForResignUpException :
    AccessDeniedException(ErrorCode.USER_NOT_ALLOWED_FOR_RESIGN_UP)
