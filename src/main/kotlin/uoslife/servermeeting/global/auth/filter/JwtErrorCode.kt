package uoslife.servermeeting.global.auth.filter

import org.springframework.http.HttpStatus

enum class JwtErrorCode(
    val message: String,
    val status: Int,
    val code: String,
) {
    TOKEN_EXPIRED_ERROR("토큰이 만료되었습니다", HttpStatus.UNAUTHORIZED.value(), "J01"),
    TOKEN_SIGNATURE_ERROR("유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED.value(), "J02"),
    TOKEN_NOT_EXIST("토큰이 존재하지 않습니다.", HttpStatus.UNAUTHORIZED.value(), "J03"),
}
