package uoslife.servermeeting.verification.exception

import uoslife.servermeeting.global.error.exception.BusinessException
import uoslife.servermeeting.global.error.exception.ErrorCode

open class EmailLimitExceededException : BusinessException {
    constructor(errorCode: ErrorCode) : super(errorCode)
    constructor(message: String, errorCode: ErrorCode) : super(message, errorCode)
}
