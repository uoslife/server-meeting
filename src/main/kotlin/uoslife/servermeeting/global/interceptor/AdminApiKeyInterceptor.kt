package uoslife.servermeeting.global.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.servlet.HandlerInterceptor
import uoslife.servermeeting.admin.exception.ApiKeyInvalidException
import uoslife.servermeeting.admin.exception.ApiKeyNotFoundException

class AdminApiKeyInterceptor(private val adminApiKey: String) : HandlerInterceptor {
    companion object {
        private const val API_KEY_HEADER = "X-API-Key"
    }

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        val apiKey = request.getHeader(API_KEY_HEADER) ?: throw ApiKeyNotFoundException()

        if (apiKey != adminApiKey) {
            throw ApiKeyInvalidException()
        }

        return true
    }
}
