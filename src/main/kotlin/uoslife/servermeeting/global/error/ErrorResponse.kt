package uoslife.servermeeting.global.error

import uoslife.servermeeting.global.error.exception.ErrorCode

data class ErrorResponse(
    val message: String,
    val status: Int,
    val code: String,
) {
  constructor(
      errorCode: ErrorCode
  ) : this(
      message = errorCode.message,
      status = errorCode.status,
      code = errorCode.code,
  )
}
