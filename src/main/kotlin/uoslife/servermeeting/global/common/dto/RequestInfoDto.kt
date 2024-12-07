package uoslife.servermeeting.global.common.dto

data class RequestInfoDto(val ip: String, val userAgent: String) {
    override fun toString(): String {
        return "ip: $ip, userAgent: $userAgent"
    }
}
