package uoslife.servermeeting.global.auth.cookie

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import uoslife.servermeeting.global.auth.exception.SessionCookieExpiredException
import uoslife.servermeeting.global.auth.exception.SessionCookieInvalidException
import uoslife.servermeeting.global.auth.exception.SessionCookieNotFoundException
import uoslife.servermeeting.global.error.ErrorResponse
import uoslife.servermeeting.global.error.exception.ErrorCode
import java.io.IOException

@Component
class CookieAuthFilter(private val authProviderService: AuthProviderService) :
    OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            // Check if the request is for a public endpoint
            if (isPublicEndpoint(request)) {
                filterChain.doFilter(request, response)
                return
            }

            // Perform the Session Cookie validation and authentication process for non-public endpoints
            val sessionCookie = authProviderService.getSessionCookieFromRequest(request)
            if (StringUtils.hasText(sessionCookie)) {
                val authentication = authProviderService.getAuthentication(sessionCookie)
                SecurityContextHolder.getContext().authentication = authentication
            }

            filterChain.doFilter(request, response)
        } catch (sessionCookieNotFoundException: SessionCookieNotFoundException) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, response, ErrorCode.SESSION_COOKIE_NOT_FOUND_IN_HEADER)
        } catch (sessionCookieExpiredException: SessionCookieExpiredException) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, response, ErrorCode.SESSION_COOKIE_EXPIRED)
        } catch (sessionCookieInvalidException: SessionCookieInvalidException) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, response, ErrorCode.SESSION_COOKIE_INVALID)
        } catch (jsonParseException: JsonParseException) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, response, ErrorCode.SESSION_COOKIE_INVALID)
        } catch (exception: Exception) {
            setErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, response, ErrorCode.INTERNAL_SERVER_ERROR)
        }
    }

    @Throws(IOException::class)
    fun setErrorResponse(
        status: HttpStatus,
        response: HttpServletResponse,
        errorCode: ErrorCode,
    ) {
        val objectMapper = ObjectMapper()
        if (!response.isCommitted) { // Check if response has been committed
            response.status = status.value()
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.writer.write(
                objectMapper.writeValueAsString(
                    ErrorResponse(errorCode),
                ),
            )
        }
    }

    private fun isPublicEndpoint(request: HttpServletRequest): Boolean {
        val requestURI = request.requestURI

        // List public endpoints
        return requestURI.contains("/api/swagger-ui/") ||
            requestURI.contains("/api/api-docs")
    }
}
