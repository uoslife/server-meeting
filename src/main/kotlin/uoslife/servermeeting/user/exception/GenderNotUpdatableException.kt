package uoslife.servermeeting.user.exception

import uoslife.servermeeting.global.error.exception.ErrorCode
import uoslife.servermeeting.global.error.exception.InvalidValueException

class GenderNotUpdatableException : InvalidValueException(ErrorCode.GENDER_NOT_CHANGE)
