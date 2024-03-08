package uoslife.servermeeting.global.auth.exception

import uoslife.servermeeting.global.error.exception.AccessDeniedException
import uoslife.servermeeting.global.error.exception.ErrorCode

class UserNotAllowedForMigration : AccessDeniedException(ErrorCode.USER_NOT_ALLOWED_FOR_MIGRATION)
