package uoslife.servermeeting.global.config

import feign.codec.ErrorDecoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uoslife.servermeeting.global.error.FeginClientErrorDecoder

@Configuration
class FeginClientConfig {

    @Bean
    fun errorDecoder(): ErrorDecoder {
        return FeginClientErrorDecoder()
    }
}
