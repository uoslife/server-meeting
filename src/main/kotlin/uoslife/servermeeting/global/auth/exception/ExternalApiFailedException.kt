package uoslife.servermeeting.global.auth.exception

import uoslife.servermeeting.global.error.exception.BusinessException
import uoslife.servermeeting.global.error.exception.ErrorCode

class ExternalApiFailedException : BusinessException(ErrorCode.EXTERNAL_API_FAILED)
