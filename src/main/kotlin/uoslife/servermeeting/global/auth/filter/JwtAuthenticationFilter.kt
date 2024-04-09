package uoslife.servermeeting.global.auth.filter

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import uoslife.servermeeting.global.auth.exception.InvalidTokenException
import uoslife.servermeeting.global.auth.exception.UnauthorizedException
import uoslife.servermeeting.global.auth.jwt.TokenProvider
import uoslife.servermeeting.global.auth.jwt.TokenType
import uoslife.servermeeting.global.error.ErrorResponse
import uoslife.servermeeting.global.error.exception.ErrorCode

class JwtAuthenticationFilter(
    private val tokenProvider: TokenProvider,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        setAuthentication(request, response)
        filterChain.doFilter(request, response)
    }

    private fun setAuthentication(request: HttpServletRequest, response: HttpServletResponse) {
        try {
            val token = tokenProvider.resolveToken(request) ?: throw UnauthorizedException()

            if (
                StringUtils.hasText(token) &&
                    tokenProvider.validateJwtToken(token, TokenType.ACCESS_SECRET)
            ) {
                val authentication = tokenProvider.getAuthentication(token)
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (e: Exception) {
            when (e) {
                is InvalidTokenException,
                is UnauthorizedException -> {
                    val errorResponse = ErrorResponse(ErrorCode.INVALID_TOKEN)
                    sendError(response, errorResponse)
                }
                else -> {
                    val errorResponse = ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR)
                    sendError(response, errorResponse)
                }
            }
        }
    }

    private fun sendError(response: HttpServletResponse, errorResponse: ErrorResponse): Unit {
        response.status = errorResponse.status
        response.characterEncoding = "utf-8"
        response.contentType = "application/json;charset-UTF-8"
        response.writer.write(convertObjectToJson(errorResponse))
    }

    @Throws(JsonProcessingException::class)
    private fun convertObjectToJson(`object`: Any?): String? {
        if (`object` == null) return null

        val mapper = ObjectMapper()
        return mapper.writeValueAsString(`object`)
    }
}
