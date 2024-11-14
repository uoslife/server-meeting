package uoslife.servermeeting.global.auth.exception

import uoslife.servermeeting.global.error.exception.ErrorCode
import uoslife.servermeeting.global.error.exception.JwtAuthenticationException

class JwtRefreshTokenExpiredException :
    JwtAuthenticationException(ErrorCode.JWT_REFRESH_TOKEN_EXPIRED)
