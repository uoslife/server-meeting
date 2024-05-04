package uoslife.servermeeting.global.error

import java.io.IOException
import org.slf4j.LoggerFactory
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.client.ResponseErrorHandler
import uoslife.servermeeting.global.auth.exception.ExternalApiFailedException
import uoslife.servermeeting.global.error.exception.BusinessException
import uoslife.servermeeting.global.error.exception.ErrorCode

@Component
class RestTemplateErrorHandler : ResponseErrorHandler {
    private val logger = LoggerFactory.getLogger(RestTemplateErrorHandler::class.java)

    @Throws(IOException::class)
    override fun hasError(response: ClientHttpResponse): Boolean {
        val statusCode = response.statusCode
        return !statusCode.is2xxSuccessful
    }

    @Throws(IOException::class)
    override fun handleError(response: ClientHttpResponse) {
        logger.info("RestTemplate Error: ")
        logger.info(response.statusCode.toString())
        logger.info(response.statusText)
        logger.info(response.body.toString())

        throw ExternalApiFailedException()
    }
}
