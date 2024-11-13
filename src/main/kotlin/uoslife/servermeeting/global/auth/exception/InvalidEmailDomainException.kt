package uoslife.servermeeting.global.auth.exception

import uoslife.servermeeting.global.error.exception.ErrorCode
import uoslife.servermeeting.global.error.exception.InvalidEmailException

class InvalidEmailDomainException : InvalidEmailException(ErrorCode.EMAIL_INVALID_DOMAIN)

