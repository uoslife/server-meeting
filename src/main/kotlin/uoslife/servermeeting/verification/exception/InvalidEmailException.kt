package uoslife.servermeeting.verification.exception

import uoslife.servermeeting.global.error.exception.ErrorCode
import uoslife.servermeeting.global.error.exception.InvalidValueException

open class InvalidEmailException : InvalidValueException {
    constructor(errorCode: ErrorCode) : super(errorCode)
    constructor(message: String, errorCode: ErrorCode) : super(message, errorCode)
}
