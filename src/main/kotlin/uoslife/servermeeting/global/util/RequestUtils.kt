package uoslife.servermeeting.global.util

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component
import uoslife.servermeeting.global.common.dto.RequestInfoDto

@Component
class RequestUtils {
    companion object {
        private const val X_FORWARDED_FOR = "X-Forwarded-For"
        private const val PROXY_CLIENT_IP = "Proxy-Client-IP"
        private const val WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP"
    }

    fun toRequestInfoDto(request: HttpServletRequest) =
        RequestInfoDto(ip = getClientIp(request), userAgent = getUserAgent(request))

    fun getClientIp(request: HttpServletRequest): String =
        when {
            request.getHeader(X_FORWARDED_FOR) != null ->
                request.getHeader(X_FORWARDED_FOR).split(",")[0]
            request.getHeader(PROXY_CLIENT_IP) != null -> request.getHeader(PROXY_CLIENT_IP)
            request.getHeader(WL_PROXY_CLIENT_IP) != null -> request.getHeader(WL_PROXY_CLIENT_IP)
            else -> request.remoteAddr
        }

    fun getUserAgent(request: HttpServletRequest): String {
        return request.getHeader("User-Agent") ?: "Unknown"
    }
}
