package uoslife.servermeeting.global.error.exception

open class EmailLimitExceededException : BusinessException {
    constructor(errorCode: ErrorCode) : super(errorCode)
    constructor(message: String, errorCode: ErrorCode) : super(message, errorCode)
}
