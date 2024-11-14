package uoslife.servermeeting.global.auth.exception

import uoslife.servermeeting.global.error.exception.ErrorCode
import uoslife.servermeeting.global.error.exception.InvalidValueException

class EmailVerificationCodeMismatchException :
    InvalidValueException(ErrorCode.EMAIL_VERIFICATION_CODE_MISMATCH)
