package uoslife.servermeeting.global.auth.exception

import uoslife.servermeeting.global.error.exception.ErrorCode

class JwtTokenExpiredException : JwtAuthenticationException(ErrorCode.JWT_TOKEN_EXPIRED)
