package uoslife.servermeeting.global.auth.exception

import uoslife.servermeeting.global.error.exception.ErrorCode
import uoslife.servermeeting.global.error.exception.InvalidValueException

open class JwtAuthenticationException : InvalidValueException {
    constructor(errorCode: ErrorCode) : super(errorCode)
    constructor(message: String, errorCode: ErrorCode) : super(message, errorCode)
}
