package uoslife.servermeeting.meetingteam.exception

import uoslife.servermeeting.global.error.exception.EntityNotFoundException
import uoslife.servermeeting.global.error.exception.ErrorCode

class PaymentNotFoundException : EntityNotFoundException(ErrorCode.PAYMENT_NOT_FOUND)
