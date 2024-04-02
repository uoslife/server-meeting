package uoslife.servermeeting.verification.exception

import uoslife.servermeeting.global.error.exception.BusinessException
import uoslife.servermeeting.global.error.exception.ErrorCode

class EmailSendFailedException : BusinessException(ErrorCode.EMAIL_SEND_FAILED)
