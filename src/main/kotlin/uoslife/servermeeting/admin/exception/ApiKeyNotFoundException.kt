package uoslife.servermeeting.admin.exception

import uoslife.servermeeting.global.error.exception.ErrorCode

class ApiKeyNotFoundException : ApiKeyAuthenticationException(ErrorCode.API_KEY_NOT_FOUND)
