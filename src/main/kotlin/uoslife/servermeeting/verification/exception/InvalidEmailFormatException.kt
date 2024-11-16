package uoslife.servermeeting.verification.exception

import uoslife.servermeeting.global.error.exception.ErrorCode

class InvalidEmailFormatException : InvalidEmailException(ErrorCode.EMAIL_INVALID_FORMAT)
