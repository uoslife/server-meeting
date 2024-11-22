package uoslife.servermeeting.payment.exception

import uoslife.servermeeting.global.error.exception.BusinessException
import uoslife.servermeeting.global.error.exception.ErrorCode

class UserAlreadyHavePaymentException : BusinessException(ErrorCode.USER_ALREADY_HAVE_PAYMENT)
