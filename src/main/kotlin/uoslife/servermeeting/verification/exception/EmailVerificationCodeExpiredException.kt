package uoslife.servermeeting.verification.exception

import uoslife.servermeeting.global.error.exception.BusinessException
import uoslife.servermeeting.global.error.exception.ErrorCode

class EmailVerificationCodeExpiredException :
    BusinessException(ErrorCode.EMAIL_VERIFICATION_CODE_EXPIRED) {}
