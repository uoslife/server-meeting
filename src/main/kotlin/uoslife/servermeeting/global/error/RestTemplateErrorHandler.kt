package uoslife.servermeeting.global.error

import org.slf4j.LoggerFactory
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.ResponseErrorHandler
import uoslife.servermeeting.global.error.exception.BusinessException
import uoslife.servermeeting.global.error.exception.ErrorCode
import java.io.IOException

class RestTemplateErrorHandler : ResponseErrorHandler {
    private val logger = LoggerFactory.getLogger(RestTemplateErrorHandler::class.java)

    @Throws(IOException::class)
    override fun hasError(response: ClientHttpResponse): Boolean {
        val statusCode = response.statusCode
        return !statusCode.is2xxSuccessful
    }

    @Throws(IOException::class)
    override fun handleError(response: ClientHttpResponse) {
        logger.info(response.statusCode.toString())
        logger.info(response.statusText)
        logger.info(response.body.toString())

        throw BusinessException(ErrorCode.EXTERNAL_API_FAILED)
    }
}
