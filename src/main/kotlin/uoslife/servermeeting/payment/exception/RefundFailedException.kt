package uoslife.servermeeting.payment.exception

import uoslife.servermeeting.global.error.exception.BusinessException
import uoslife.servermeeting.global.error.exception.ErrorCode

class RefundFailedException : BusinessException(ErrorCode.PAYMENT_REFUND_FAILED)
