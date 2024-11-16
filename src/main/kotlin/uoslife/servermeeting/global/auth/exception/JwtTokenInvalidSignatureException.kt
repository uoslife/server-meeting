package uoslife.servermeeting.global.auth.exception

import uoslife.servermeeting.global.error.exception.ErrorCode

class JwtTokenInvalidSignatureException :
    JwtAuthenticationException(ErrorCode.JWT_TOKEN_INVALID_SIGNATURE)
