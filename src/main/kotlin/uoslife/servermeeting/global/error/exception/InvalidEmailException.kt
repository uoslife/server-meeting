package uoslife.servermeeting.global.error.exception

open class InvalidEmailException : InvalidValueException {
    constructor(errorCode: ErrorCode) : super(errorCode)
    constructor(message: String, errorCode: ErrorCode) : super(message, errorCode)
}
