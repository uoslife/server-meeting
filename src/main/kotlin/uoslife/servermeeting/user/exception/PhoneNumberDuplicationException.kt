package uoslife.servermeeting.user.exception

import uoslife.servermeeting.global.error.exception.BusinessException
import uoslife.servermeeting.global.error.exception.ErrorCode

class PhoneNumberDuplicationException : BusinessException(ErrorCode.PHONE_NUMBER_DUPLICATED)
