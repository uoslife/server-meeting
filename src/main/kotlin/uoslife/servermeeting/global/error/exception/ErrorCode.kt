package uoslife.servermeeting.global.error.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(val code: String, val message: String, var status: Int) {
    // Common
    INVALID_INPUT_VALUE("C01", "Invalid Input Value.", HttpStatus.BAD_REQUEST.value()),
    METHOD_NOT_ALLOWED("C02", "Invalid Method Type.", HttpStatus.METHOD_NOT_ALLOWED.value()),
    ENTITY_NOT_FOUND("C03", "Entity Not Found.", HttpStatus.BAD_REQUEST.value()),
    INTERNAL_SERVER_ERROR(
        "C04",
        "Internal Server Error.",
        HttpStatus.INTERNAL_SERVER_ERROR.value()
    ),
    INVALID_TYPE_VALUE("C05", " Invalid Type Value.", HttpStatus.BAD_REQUEST.value()),

    // User
    USER_ACCESS_DENIED("U01", "User Access is Denied.", HttpStatus.UNAUTHORIZED.value()),
    USER_NOT_FOUND("U02", "User is not Found.", HttpStatus.BAD_REQUEST.value()),
    EMAIL_DUPLICATION("U03", "Email is Duplicated.", HttpStatus.BAD_REQUEST.value()),
    NICKNAME_DUPLICATION("U04", "Nickname is Duplicated.", HttpStatus.BAD_REQUEST.value()),
    USER_ALREADY_RESET("U05", "User data is already reset.", HttpStatus.BAD_REQUEST.value()),
    PHONE_NUMBER_NOT_FOUND(
        "U06",
        "User's phone number is not fount",
        HttpStatus.BAD_REQUEST.value()
    ),
    // User - Cookie
    SESSION_COOKIE_EXPIRED("AT01", "Access Token is Expired", HttpStatus.UNAUTHORIZED.value()),
    SESSION_COOKIE_INVALID("AT02", "Access Token is Invalid.", HttpStatus.UNAUTHORIZED.value()),
    SESSION_COOKIE_NOT_FOUND_IN_HEADER(
        "AT03",
        "Access Token is not Found in Header.",
        HttpStatus.UNAUTHORIZED.value()
    ),

    // Department
    DEPARTMENT_NOT_FOUND("D01", "Department is not Found.", HttpStatus.BAD_REQUEST.value()),

    // Meeting
    USER_TEAM_NOT_FOUND("M01", "User Team is not Found.", HttpStatus.BAD_REQUEST.value()),
    USER_ALREADY_HAVE_TEAM("M02", "User already have Team.", HttpStatus.BAD_REQUEST.value()),
    ONLY_TEAM_LEADER_CAN_CREATE_TEAM(
        "M03",
        "Only Team Leader can Create Team.",
        HttpStatus.BAD_REQUEST.value()
    ),
    IN_SINGLE_MEETING_TEAM_NO_JOIN_TEAM(
        "M04",
        "In Single Meeting Team, you can't Join Team.",
        HttpStatus.BAD_REQUEST.value(),
    ),
    IN_SINGLE_MEETING_TEAM_ONLY_ONE_USER(
        "M05",
        "In Single Meeting Team, only One User Exist.",
        HttpStatus.BAD_REQUEST.value(),
    ),
    MEETING_TEAM_NOT_FOUND("M06", "Meeting Team is not Found.", HttpStatus.BAD_REQUEST.value()),
    INFORMATION_NOT_FOUND("M07", "Information is not Found.", HttpStatus.BAD_REQUEST.value()),
    PREFERENCE_NOT_FOUND("M08", "Preference is not Found.", HttpStatus.BAD_REQUEST.value()),
    ONLY_TEAM_LEADER_CAN_UPDATE_TEAM_INFORMATION(
        "M09",
        "Only Team Leader can Update Team Information.",
        HttpStatus.BAD_REQUEST.value(),
    ),
    ONLY_TEAM_LEADER_CAN_DELETE_TEAM(
        "M10",
        "Only Team Leader can Delete Team.",
        HttpStatus.BAD_REQUEST.value()
    ),
    TEAM_NAME_LEAST_2_CHARACTER(
        "M11",
        "Team Name must be at Least 2 Characters.",
        HttpStatus.BAD_REQUEST.value()
    ),
    TEAM_CODE_GENERATE_FAILED(
        "M12",
        "Team Code Generate is Failed.",
        HttpStatus.INTERNAL_SERVER_ERROR.value()
    ),
    TEAM_CODE_INVALID_FORMAT("M13", "Team Code is Invalid Format.", HttpStatus.BAD_REQUEST.value()),
    TEAM_FULL("M14", "Team is Full.", HttpStatus.BAD_REQUEST.value()),
    USER_NOT_IN_TEAM("M15", "User is not in Team.", HttpStatus.BAD_REQUEST.value()),
    TEAM_LEADER_NOT_FOUND("M16", "Team Leader is not Found.", HttpStatus.BAD_REQUEST.value()),
    TEAM_CONSIST_OF_SAME_GENDER(
        "M17",
        "Team must consist of Same Gender",
        HttpStatus.BAD_REQUEST.value()
    ),

    // Match
    MATCH_NOT_FOUND("MT01", "Match is not Found.", HttpStatus.BAD_REQUEST.value()),

    // External API
    EXTERNAL_API_FAILED(
        "E01",
        "External API Request is failed.",
        HttpStatus.INTERNAL_SERVER_ERROR.value()
    ),

    // payment
    AlREADY_EXISTS_PAYMENT("P01", "User has already paid.", HttpStatus.BAD_REQUEST.value()),
    PAYMENT_INFORMATION_INVALID(
        "P02",
        "Payment information is invalid.",
        HttpStatus.BAD_REQUEST.value()
    ),
    PAYMENT_NOT_FOUND("P03", "Payment is not found", HttpStatus.BAD_REQUEST.value())
}
