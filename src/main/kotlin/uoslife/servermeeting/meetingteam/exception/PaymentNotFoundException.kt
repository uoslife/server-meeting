package uoslife.servermeeting.meetingteam.exception

import uoslife.servermeeting.global.error.exception.EntityNotFoundException
import uoslife.servermeeting.global.error.exception.ErrorCode
import uoslife.servermeeting.global.error.exception.InvalidValueException

class PaymentNotFoundException :
    EntityNotFoundException(ErrorCode.PAYMENT_NOT_FOUND)
