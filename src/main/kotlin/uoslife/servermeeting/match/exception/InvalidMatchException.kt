package uoslife.servermeeting.match.exception

import uoslife.servermeeting.global.error.exception.ErrorCode
import uoslife.servermeeting.global.error.exception.InvalidValueException

class InvalidMatchException : InvalidValueException(ErrorCode.INVALID_MATCH)
