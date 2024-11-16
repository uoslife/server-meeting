package uoslife.servermeeting.verification.exception

import uoslife.servermeeting.global.error.exception.ErrorCode

class DailyEmailSendLimitExceededException :
    EmailLimitExceededException(ErrorCode.EMAIL_DAILY_SEND_LIMIT_EXCEEDED)
