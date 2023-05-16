package uoslife.servermeeting.global.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI {
        val info: Info =
            Info().title("UOSLIFE Meeting API")
                .description("UOSLIFE Meeting API Documentation")
                .version("v0.0.1")

        val jwtSchemeName = "Session Cookie"
        val auth = SecurityScheme()
            .type(SecurityScheme.Type.APIKEY).`in`(SecurityScheme.In.COOKIE).name("session")
        val securityRequirement = SecurityRequirement().addList(jwtSchemeName)

        return OpenAPI()
            .components(Components().addSecuritySchemes(jwtSchemeName, auth))
            .addSecurityItem(securityRequirement)
            .info(info)
    }
}
