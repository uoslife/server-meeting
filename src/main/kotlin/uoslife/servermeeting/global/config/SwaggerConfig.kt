package uoslife.servermeeting.global.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    companion object {
        const val ACCESS_TOKEN = "Access Token"
    }

    private val accessToken: SecurityScheme =
        SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .name(ACCESS_TOKEN)
            .description("시대팅 서비스 인증을 위한 토큰")

    @Bean
    fun openAPI(): OpenAPI {
        val info: Info =
            Info()
                .title("UOSLIFE Meeting API")
                .description("UOSLIFE Meeting API Documentation")
                .version("v4.0.1")

        val securityRequirement = SecurityRequirement().addList(ACCESS_TOKEN)

        return OpenAPI()
            .servers(
                listOf(
                    Server().url("https://uosmeeting.uoslife.com"),
                    Server().url("https://meeting.alpha.uoslife.com"),
                    Server().url("http://localhost:8081"),
                    Server().url("https://meeting.uoslife.com"),
                )
            )
            .components(Components().addSecuritySchemes(ACCESS_TOKEN, accessToken))
            .addSecurityItem(securityRequirement)
            .info(info)
    }
}
