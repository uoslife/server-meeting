package uoslife.servermeeting.domain.user.domain.exception

import uoslife.servermeeting.global.error.exception.EntityNotFoundException
import uoslife.servermeeting.global.error.exception.ErrorCode

class UserNotFoundException : EntityNotFoundException(ErrorCode.USER_NOT_FOUND)
