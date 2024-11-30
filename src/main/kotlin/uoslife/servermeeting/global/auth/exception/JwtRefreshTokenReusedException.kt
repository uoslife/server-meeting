package uoslife.servermeeting.global.auth.exception

import uoslife.servermeeting.global.error.exception.ErrorCode

class JwtRefreshTokenReusedException :
    JwtAuthenticationException(ErrorCode.JWT_REFRESH_TOKEN_REUSED)
