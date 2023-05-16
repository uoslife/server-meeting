package uoslife.servermeeting.global.error.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(val code: String, val message: String, var status: Int) {
    // Common
    INVALID_INPUT_VALUE("C01", "Invalid Input Value.", HttpStatus.BAD_REQUEST.value()),
    METHOD_NOT_ALLOWED("C02", "Invalid Method Type.", HttpStatus.METHOD_NOT_ALLOWED.value()),
    ENTITY_NOT_FOUND("C03", "Entity Not Found.", HttpStatus.BAD_REQUEST.value()),
    INTERNAL_SERVER_ERROR("C04", "Internal Server Error.", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    INVALID_TYPE_VALUE("C05", " Invalid Type Value.", HttpStatus.BAD_REQUEST.value()),

    // User
    USER_ACCESS_DENIED("U01", "User Access is Denied.", HttpStatus.UNAUTHORIZED.value()),
    USER_NOT_FOUND("U02", "User is not Found.", HttpStatus.BAD_REQUEST.value()),
    EMAIL_DUPLICATION("U03", "Email is Duplicated.", HttpStatus.BAD_REQUEST.value()),
    NICKNAME_DUPLICATION("U04", "Nickname is Duplicated.", HttpStatus.BAD_REQUEST.value()),

    // User - Cookie
    SESSION_COOKIE_EXPIRED("AT01", "Access Token is Expired", HttpStatus.UNAUTHORIZED.value()),
    SESSION_COOKIE_INVALID("AT02", "Access Token is Invalid.", HttpStatus.UNAUTHORIZED.value()),
    SESSION_COOKIE_NOT_FOUND_IN_HEADER("AT03", "Access Token is not Found in Header.", HttpStatus.UNAUTHORIZED.value()),

    // Department
    DEPARTMENT_NOT_FOUND("D01", "Department is not Found.", HttpStatus.BAD_REQUEST.value()),

    // External API
    EXTERNAL_API_FAILED("E01", "External API Request is failed.", HttpStatus.INTERNAL_SERVER_ERROR.value()),
}
