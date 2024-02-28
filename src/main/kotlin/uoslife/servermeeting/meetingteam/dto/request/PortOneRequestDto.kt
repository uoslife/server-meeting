package uoslife.servermeeting.meetingteam.dto.request

class PortOneRequestDto {
    data class AccessTokenRequest(
        var imp_key: String,
        var imp_secret: String,
    )
}
