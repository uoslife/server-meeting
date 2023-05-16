package uoslife.servermeeting.global.auth

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import uoslife.servermeeting.global.error.ErrorResponse
import uoslife.servermeeting.global.error.exception.ErrorCode

class RestAuthenticationEntryPoint : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authException: AuthenticationException?,
    ) {
        val objectMapper = ObjectMapper()
        val authorization = request!!.getHeader("Authorization")

        if (authorization == null || authorization == "" || authorization == "Bearer ") {
            response!!.status = ErrorCode.USER_ACCESS_DENIED.status
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.writer.write(
                objectMapper.writeValueAsString(
                    ErrorResponse(ErrorCode.USER_ACCESS_DENIED),
                ),
            )
        }
    }
}
