package uoslife.servermeeting.verification.exception

import uoslife.servermeeting.global.error.exception.ErrorCode
import uoslife.servermeeting.global.error.exception.InvalidValueException

class VerificationCodeNotMatchException: InvalidValueException(ErrorCode.VERIFICATION_CODE_NOT_MATCH) {
}
