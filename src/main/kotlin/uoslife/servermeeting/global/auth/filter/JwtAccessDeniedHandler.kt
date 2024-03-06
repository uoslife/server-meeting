package com.uoslife.core.auth.filter

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler

class JwtAccessDeniedHandler : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        accessDeniedException: AccessDeniedException?,
    ) {
        response?.sendError(HttpServletResponse.SC_FORBIDDEN)
        response?.characterEncoding = "utf-8"
        response?.contentType = "application/json;charset-UTF-8"
        response?.status = HttpStatus.FORBIDDEN.value()
    }
}
