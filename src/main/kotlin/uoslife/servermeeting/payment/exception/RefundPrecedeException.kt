package uoslife.servermeeting.payment.exception

import uoslife.servermeeting.global.error.exception.BusinessException
import uoslife.servermeeting.global.error.exception.ErrorCode

class RefundPrecedeException : BusinessException(ErrorCode.PAYMENT_NOT_REFUND)
