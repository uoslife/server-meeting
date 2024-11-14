package uoslife.servermeeting.global.auth.exception

import uoslife.servermeeting.global.error.exception.BusinessException
import uoslife.servermeeting.global.error.exception.ErrorCode

class EmailDeliveryFailedException : BusinessException(ErrorCode.EMAIL_SEND_FAILED)
