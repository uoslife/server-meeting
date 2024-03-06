package uoslife.servermeeting.meetingteam.exception

import uoslife.servermeeting.global.error.exception.ErrorCode
import uoslife.servermeeting.global.error.exception.InvalidValueException

class PaymentInValidException : InvalidValueException(ErrorCode.PAYMENT_INVALID)
