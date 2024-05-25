package uoslife.servermeeting.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.CommonsRequestLoggingFilter

@Configuration
class WebLoggingConfig {

    @Bean
    fun commonsRequestLoggingFilter(): CommonsRequestLoggingFilter {
        val filter = CommonsRequestLoggingFilter()
        filter.setIncludeClientInfo(true)
        filter.setIncludeHeaders(true)
        filter.setHeaderPredicate { header -> header.lowercase() != "authorization" }
        filter.setIncludePayload(true)
        filter.setIncludeQueryString(true)
        filter.setMaxPayloadLength(1000)
        return filter
    }
}
