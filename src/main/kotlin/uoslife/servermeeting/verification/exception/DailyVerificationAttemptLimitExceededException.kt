package uoslife.servermeeting.verification.exception

import uoslife.servermeeting.global.error.exception.ErrorCode

class DailyVerificationAttemptLimitExceededException :
    EmailLimitExceededException(ErrorCode.EMAIL_DAILY_VERIFY_LIMIT_EXCEEDED)
