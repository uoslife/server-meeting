package uoslife.servermeeting.user.exception

import uoslife.servermeeting.global.error.exception.ErrorCode
import uoslife.servermeeting.global.error.exception.InvalidValueException

class GenderNotUpdateException : InvalidValueException(ErrorCode.GENDER_NOT_CHANGE)
