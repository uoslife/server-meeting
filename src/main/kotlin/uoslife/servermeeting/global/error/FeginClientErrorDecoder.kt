package uoslife.servermeeting.global.error

import feign.Response
import feign.codec.ErrorDecoder
import java.lang.Exception
import org.slf4j.LoggerFactory
import uoslife.servermeeting.global.auth.exception.ExternalApiFailedException

class FeginClientErrorDecoder : ErrorDecoder {
    companion object {
        private val logger = LoggerFactory.getLogger(FeginClientErrorDecoder::class.java)
    }
    override fun decode(methodKey: String, response: Response): Exception {
        logger.info("Error Description: " + response.reason())
        return ExternalApiFailedException()
    }
}
