package uoslife.servermeeting.global.auth.exception

import uoslife.servermeeting.global.error.exception.ErrorCode
import uoslife.servermeeting.global.error.exception.JwtAuthenticationException

class JwtTokenInvalidSignatureException :
    JwtAuthenticationException(ErrorCode.JWT_TOKEN_INVALID_SIGNATURE)
