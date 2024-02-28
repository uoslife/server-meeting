package uoslife.servermeeting.certification.exception

import uoslife.servermeeting.global.error.exception.EntityNotFoundException
import uoslife.servermeeting.global.error.exception.ErrorCode

class VerificationNotFoundException : EntityNotFoundException(ErrorCode.CERT_NOT_FOUND)
