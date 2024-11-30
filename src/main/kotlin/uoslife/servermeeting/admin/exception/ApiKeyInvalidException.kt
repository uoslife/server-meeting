package uoslife.servermeeting.admin.exception

import uoslife.servermeeting.global.error.exception.ErrorCode

class ApiKeyInvalidException : ApiKeyAuthenticationException(ErrorCode.API_KEY_INVALID)
