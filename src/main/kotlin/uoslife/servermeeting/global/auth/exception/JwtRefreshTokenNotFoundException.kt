package uoslife.servermeeting.global.auth.exception

import uoslife.servermeeting.global.error.exception.ErrorCode
import uoslife.servermeeting.global.error.exception.JwtAuthenticationException

class JwtRefreshTokenNotFoundException :
    JwtAuthenticationException(ErrorCode.JWT_REFRESH_TOKEN_NOT_FOUND)
