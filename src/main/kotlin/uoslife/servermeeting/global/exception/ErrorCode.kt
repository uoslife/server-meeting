package uoslife.servermeeting.global.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(val httpStatus: HttpStatus, val detail: String) {
    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "Invalid Input Value"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed"),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "Entity Not Found"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "Duplicate Resource"),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User Not Found"),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "User Already Exists"),

    // Match
    MATCH_NOT_FOUND(HttpStatus.NOT_FOUND, "Match Not Found"),
    MATCH_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "Match Already Exists"),

    // Report
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "Report Not Found"),
    REPORT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "Report Already Exists"),

    // Compatibility
    COMPATIBILITY_NOT_FOUND(HttpStatus.NOT_FOUND, "Compatibility Not Found"),
    COMPATIBILITY_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "Compatibility Already Exists"),

    // CompatibilityPriority
    COMPATIBILITY_PRIORITY_NOT_FOUND(HttpStatus.NOT_FOUND, "CompatibilityPriority Not Found"),
    COMPATIBILITY_PRIORITY_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "CompatibilityPriority Already Exists"),

}