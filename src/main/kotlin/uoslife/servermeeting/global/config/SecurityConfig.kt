package uoslife.servermeeting.global.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
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
import uoslife.servermeeting.global.auth.jwt.JwtAuthenticationProvider
import uoslife.servermeeting.global.auth.service.UOSLIFEAccountService

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val authProvider: JwtAuthenticationProvider,
    private val accountService: UOSLIFEAccountService,
    @Qualifier("handlerExceptionResolver") private val resolver: HandlerExceptionResolver,
) {
    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain? {
        http.cors().configurationSource(configurationSource())
        http
            .httpBasic()
            .disable()
            .csrf()
            .disable()
            .cors()
            .configurationSource(configurationSource())
            .and()
            .formLogin()
            .disable()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .headers()
            .frameOptions()
            .disable()
            .and()
            .exceptionHandling() // 인증, 인가가 되지 않은 요청 발생시
            .authenticationEntryPoint(JwtAuthenticationEntryPoint(resolver))
            .accessDeniedHandler(JwtAccessDeniedHandler())
            .and()
            .authorizeHttpRequests()
            .requestMatchers(CorsUtils::isPreFlightRequest)
            .permitAll() // CORS preflight 요청 허용
            .requestMatchers("/api/**")
            .hasRole("USER") // 모든 api 요청에 대해 권한 필요

        http.addFilterBefore(
            JwtAuthenticationFilter(accountService),
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
        val authenticationManagerBuilder =
            http.getSharedObject(AuthenticationManagerBuilder::class.java)
        authenticationManagerBuilder.authenticationProvider(authProvider)
        return authenticationManagerBuilder.build()
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        // 토큰 검사 미실시 리스트
        return WebSecurityCustomizer { web: WebSecurity ->
            web.ignoring()
                .requestMatchers("/swagger-ui/**")
                .requestMatchers("/meeting/actuator/health/**")
        }
    }

    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder? {
        return BCryptPasswordEncoder()
    }
}
