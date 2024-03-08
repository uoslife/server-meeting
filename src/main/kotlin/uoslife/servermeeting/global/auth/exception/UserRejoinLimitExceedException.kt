package uoslife.servermeeting.global.auth.exception

import uoslife.servermeeting.global.error.exception.AccessDeniedException
import uoslife.servermeeting.global.error.exception.ErrorCode

class UserRejoinLimitExceedException : AccessDeniedException(ErrorCode.USER_REJOIN_LIMIT_EXCEED)
