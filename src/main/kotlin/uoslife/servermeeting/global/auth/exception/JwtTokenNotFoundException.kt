package uoslife.servermeeting.global.auth.exception

import uoslife.servermeeting.global.error.exception.ErrorCode

class JwtTokenNotFoundException : JwtAuthenticationException(ErrorCode.JWT_TOKEN_NOT_FOUND)
