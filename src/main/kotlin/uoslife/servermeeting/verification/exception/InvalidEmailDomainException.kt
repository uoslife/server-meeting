package uoslife.servermeeting.verification.exception

import uoslife.servermeeting.global.error.exception.ErrorCode

class InvalidEmailDomainException : InvalidEmailException(ErrorCode.EMAIL_INVALID_DOMAIN)
