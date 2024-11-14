package uoslife.servermeeting.global.error.exception

open class JwtAuthenticationException : InvalidValueException {
    constructor(errorCode: ErrorCode) : super(errorCode)
    constructor(message: String, errorCode: ErrorCode) : super(message, errorCode)
}
