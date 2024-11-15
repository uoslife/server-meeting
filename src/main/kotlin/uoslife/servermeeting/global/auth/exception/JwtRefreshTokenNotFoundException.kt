package uoslife.servermeeting.global.auth.exception

import uoslife.servermeeting.global.error.exception.ErrorCode

class JwtRefreshTokenNotFoundException :
    JwtAuthenticationException(ErrorCode.JWT_REFRESH_TOKEN_NOT_FOUND)
