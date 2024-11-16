package uoslife.servermeeting.global.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.CorsUtils
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.servlet.HandlerExceptionResolver
import uoslife.servermeeting.global.auth.filter.JwtAccessDeniedHandler
import uoslife.servermeeting.global.auth.filter.JwtAuthenticationEntryPoint
import uoslife.servermeeting.global.auth.filter.JwtAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    @Qualifier("handlerExceptionResolver") private val resolver: HandlerExceptionResolver,
) {
    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain? {
        http
            .httpBasic { http -> http.disable() }
            .csrf { csrf -> csrf.disable() }
            .cors { cors -> cors.configurationSource(configurationSource()) }
            .formLogin { formLogin -> formLogin.disable() }
            .sessionManagement { sessionPolicy ->
                sessionPolicy.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .headers { headers -> headers.frameOptions().disable() }
            .exceptionHandling {
                it.authenticationEntryPoint(JwtAuthenticationEntryPoint(resolver))
                    .accessDeniedHandler(JwtAccessDeniedHandler())
            } // 인증, 인가가 되지 않은 요청 발생시
            .authorizeHttpRequests {
                it.requestMatchers(CorsUtils::isPreFlightRequest)
                    .permitAll()
                    .requestMatchers("/api/**")
                    .hasRole("USER")
            } // CORS preflight 요청 허용

        http.addFilterBefore(
            jwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter::class.java
        )

        return http.build()
    }
    @Bean
    fun configurationSource(): CorsConfigurationSource? {
        val configuration = CorsConfiguration()

        configuration.allowedOrigins =
            listOf(
                "http://localhost:8081",
                "http://localhost:3000",
                "https://uosmeeting.uoslife.net",
                "https://uoslife.com",
                "https://meeting.uoslife.com",
                "https://meeting.alpha.uoslife.com",
                "http://localhost:5173",
                "https://localhost:5173",
            )
        configuration.allowedMethods =
            mutableListOf("HEAD", "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        configuration.addAllowedHeader("*")
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)

        return source
    }

    @Bean
    fun authenticationManager(
        authConfig: AuthenticationConfiguration,
        http: HttpSecurity
    ): AuthenticationManager {
        return authConfig.authenticationManager
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        // 토큰 검사 미실시 리스트
        return WebSecurityCustomizer { web: WebSecurity ->
            web.ignoring()
                .requestMatchers("/swagger-ui/**")
                .requestMatchers("/meeting/actuator/health/**")
                .requestMatchers("/api/user/isDuplicatedKakaoTalkId") // 카카오톡 중복 확인
                .requestMatchers("/api/payment/refund/match")
                .requestMatchers("/api/payment/portone-webhook")
                .requestMatchers("/api/auth/reissue")
                .requestMatchers("/api/verification/send-email")
                .requestMatchers("/api/verification/verify-email")
        }
    }
}
