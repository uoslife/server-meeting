package uoslife.servermeeting.global.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import uoslife.servermeeting.global.interceptor.AdminApiKeyInterceptor

@Configuration
class WebConfig : WebMvcConfigurer {
    @Value("\${api.admin.key}") private lateinit var adminApiKey: String

    override fun addCorsMappings(registry: CorsRegistry) {
        registry
            .addMapping("/**")
            .allowedOrigins(
                "http://localhost:8081",
                "http://localhost:3000",
                "https://uoslife.com",
                "https://meeting.uoslife.com",
                "https://meeting.alpha.uoslife.com",
                "http://localhost:5173",
                "https://localhost:5173",
            )
            .allowedMethods("HEAD", "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            .allowCredentials(true)
            .allowedHeaders("*")
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry
            .addInterceptor(AdminApiKeyInterceptor(adminApiKey))
            .addPathPatterns("/api/admin/**")
    }
}
