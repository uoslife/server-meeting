package uoslife.servermeeting.meetingteam.exception

import uoslife.servermeeting.global.error.exception.AccessDeniedException
import uoslife.servermeeting.global.error.exception.ErrorCode
import uoslife.servermeeting.global.error.exception.InvalidValueException

class PaymentInformationInvalidException :
    InvalidValueException(ErrorCode.PAYMENT_INFORMATION_INVALID)
