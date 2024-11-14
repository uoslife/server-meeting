package uoslife.servermeeting.global.auth.exception

import uoslife.servermeeting.global.error.exception.ErrorCode
import uoslife.servermeeting.global.error.exception.JwtAuthenticationException

class JwtTokenInvalidFormatException :
    JwtAuthenticationException(ErrorCode.JWT_TOKEN_INVALID_FORMAT)
