package uoslife.servermeeting.global.error

import feign.Response
import feign.codec.ErrorDecoder
import java.lang.Exception
import uoslife.servermeeting.global.auth.exception.ExternalApiFailedException

class FeginClientErrorDecoder : ErrorDecoder {
    override fun decode(methodKey: String, response: Response): Exception {
        return ExternalApiFailedException()
    }
}
