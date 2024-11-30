package uoslife.servermeeting.admin.exception

import uoslife.servermeeting.global.error.exception.ErrorCode
import uoslife.servermeeting.global.error.exception.InvalidValueException

open class ApiKeyAuthenticationException : InvalidValueException {
    constructor(errorCode: ErrorCode) : super(errorCode)
}
