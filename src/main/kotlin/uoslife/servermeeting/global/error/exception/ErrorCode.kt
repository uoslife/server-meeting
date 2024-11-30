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

    // User
    USER_ACCESS_DENIED("U01", "User Access is Denied.", HttpStatus.UNAUTHORIZED.value()),
    USER_NOT_FOUND("U02", "User is not Found.", HttpStatus.BAD_REQUEST.value()),
    PHONE_NUMBER_NOT_FOUND("U06", "Phone Number is not found.", HttpStatus.BAD_REQUEST.value()),
    USER_NOT_AUTHORIZED("U08", "User is not authorized.", HttpStatus.UNAUTHORIZED.value()),
    GENDER_NOT_FOUND("U09", "Gender is not selected.", HttpStatus.BAD_REQUEST.value()),
    GENDER_NOT_CHANGE("U09", "Gender is not changeable.", HttpStatus.BAD_REQUEST.value()),
    PRECONDITION_FAILED(
        "U10",
        "UserInfo is not completed.",
        HttpStatus.PRECONDITION_FAILED.value()
    ),
    KAKAO_TALK_ID_DUPLICATED("U11", "Kakao Talk ID is Duplicated.", HttpStatus.BAD_REQUEST.value()),
    USER_TEAM_NOT_FOUND("U12", "UserTeam is not Found.", HttpStatus.BAD_REQUEST.value()),

    // User - Token
    INVALID_TOKEN("T01", "Token is not valid.", HttpStatus.UNAUTHORIZED.value()),

    // Meeting
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
    INVALID_TEAM_NAME("M11", "Team name is invalid.", HttpStatus.BAD_REQUEST.value()),
    TEAM_CODE_GENERATE_FAILED(
        "M12",
        "Team Code Generate is Failed.",
        HttpStatus.INTERNAL_SERVER_ERROR.value()
    ),
    TEAM_CODE_INVALID_FORMAT("M13", "Team Code is Invalid Format.", HttpStatus.BAD_REQUEST.value()),
    TEAM_FULL("M14", "Team is Full.", HttpStatus.BAD_REQUEST.value()),
    TEAM_LEADER_NOT_FOUND("M16", "Team Leader is not Found.", HttpStatus.BAD_REQUEST.value()),
    TEAM_CONSIST_OF_SAME_GENDER(
        "M17",
        "Team must consist of Same Gender",
        HttpStatus.BAD_REQUEST.value()
    ),
    INVALID_MESSAGE_LENGTH(
        "M18",
        "Message length must be over 10.",
        HttpStatus.BAD_REQUEST.value()
    ),
    // Match
    MATCH_NOT_FOUND("MT01", "Match is not Found.", HttpStatus.BAD_REQUEST.value()),
    ONLY_TEAM_LEADER_CAN_GET_MATCH(
        "MT02",
        "Only Team Leader Can Get Match.",
        HttpStatus.BAD_REQUEST.value()
    ),
    ONLY_TEAM_LEADER_CAN_CREATE_PAYMENT(
        "M10",
        "Only Team Leader can Create Payment.",
        HttpStatus.BAD_REQUEST.value()
    ),
    UNAUTHORIZED_TEAM_ACCESS(
        "MT03",
        "User does not have access to this team.",
        HttpStatus.FORBIDDEN.value()
    ),
    UNAUTHORIZED_MATCH_ACCESS(
        "MT04",
        "User does not have access to this match.",
        HttpStatus.FORBIDDEN.value()
    ),

    // External API
    EXTERNAL_API_FAILED(
        "E01",
        "External API Request is failed.",
        HttpStatus.INTERNAL_SERVER_ERROR.value()
    ),

    // Payment
    PAYMENT_NOT_FOUND("P01", "Payment is not Found.", HttpStatus.BAD_REQUEST.value()),
    PAYMENT_INVALID("P03", "Payment is Invalid.", HttpStatus.BAD_REQUEST.value()),
    USER_ALREADY_HAVE_PAYMENT("P04", "User already have Payment.", HttpStatus.BAD_REQUEST.value()),
    PAYMENT_NOT_REFUND(
        "P05",
        "User Payment refund is not completed.",
        HttpStatus.PRECONDITION_FAILED.value()
    ),

    // Email Verification
    EMAIL_INVALID_FORMAT("E01", "Invalid email format.", HttpStatus.BAD_REQUEST.value()),
    EMAIL_INVALID_DOMAIN("E02", "Email domain is not allowed.", HttpStatus.BAD_REQUEST.value()),
    EMAIL_VERIFICATION_CODE_MISMATCH("E03", "인증 코드가 일치하지 않습니다.", HttpStatus.BAD_REQUEST.value()),
    EMAIL_DAILY_SEND_LIMIT_EXCEEDED(
        "E04",
        "일일 전송 횟수를 초과했습니다.(5/5)",
        HttpStatus.TOO_MANY_REQUESTS.value()
    ),
    EMAIL_DAILY_VERIFY_LIMIT_EXCEEDED(
        "E05",
        "일일 인증 횟수를 초과했습니다.(5/5)",
        HttpStatus.TOO_MANY_REQUESTS.value()
    ),
    EMAIL_SEND_FAILED("E06", "Failed to send email.", HttpStatus.INTERNAL_SERVER_ERROR.value()),

    // JWT
    JWT_TOKEN_NOT_FOUND("J001", "Token not found", HttpStatus.UNAUTHORIZED.value()),
    JWT_TOKEN_EXPIRED("J002", "Token has expired", HttpStatus.UNAUTHORIZED.value()),
    JWT_TOKEN_INVALID_FORMAT("J003", "Invalid token format", HttpStatus.UNAUTHORIZED.value()),
    JWT_TOKEN_INVALID_SIGNATURE("J004", "Invalid token signature", HttpStatus.UNAUTHORIZED.value()),
    JWT_REFRESH_TOKEN_NOT_FOUND("J005", "Refresh token not found", HttpStatus.UNAUTHORIZED.value()),
    JWT_REFRESH_TOKEN_EXPIRED("J006", "Refresh token has expired", HttpStatus.UNAUTHORIZED.value()),
}
