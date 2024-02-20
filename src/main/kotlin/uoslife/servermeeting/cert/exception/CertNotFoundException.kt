package uoslife.servermeeting.cert.exception

import uoslife.servermeeting.global.error.exception.EntityNotFoundException
import uoslife.servermeeting.global.error.exception.ErrorCode

class CertNotFoundException : EntityNotFoundException(ErrorCode.CERT_NOT_FOUND)
