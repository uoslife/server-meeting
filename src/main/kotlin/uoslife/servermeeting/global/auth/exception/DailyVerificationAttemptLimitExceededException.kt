package uoslife.servermeeting.global.auth.exception

import uoslife.servermeeting.global.error.exception.EmailLimitExceededException
import uoslife.servermeeting.global.error.exception.ErrorCode

class DailyVerificationAttemptLimitExceededException :
    EmailLimitExceededException(ErrorCode.EMAIL_DAILY_VERIFY_LIMIT_EXCEEDED)
