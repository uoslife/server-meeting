package uoslife.servermeeting.global.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins(
                "http://localhost:8081",
                "http://localhost:3000",
                "https://www.uoslife.com",
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD")
            .allowCredentials(true)
    }
}
