package uoslife.servermeeting.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.CorsUtils
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig() {
    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain? {
        http.cors().configurationSource(configurationSource())

        http.httpBasic().disable()
            .csrf().disable()
            .cors().configurationSource(configurationSource())
            .and()
            .formLogin().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .headers().frameOptions().disable()
            .and()
            .exceptionHandling()
//            .authenticationEntryPoint(
//                RestAuthenticationEntryPoint(),
//            ) // 인증, 인가가 되지 않은 요청 발생시
            //            .authenticationEntryPoint(
            //                RestAuthenticationEntryPoint(),
            //            ) // 인증, 인가가 되지 않은 요청 발생시
            .and()
            .authorizeHttpRequests()
            .requestMatchers(CorsUtils::isPreFlightRequest).permitAll() // CORS preflight 요청 허용
            .requestMatchers("/api/swagger-ui/**", "/api/api-docs/**","/api/verification/**", "/api/user/check").permitAll() // Swagger 허용 url
            .requestMatchers("/api/**").hasRole("USER") // 모든 api 요청에 대해 권한 필요

//        http
//            .addFilterBefore(cookieAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
        //        http
        //            .addFilterBefore(JwtAuthenticationFilter(jwtUtils), UsernamePasswordAuthenticationFilter::class.java)

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
            )
        configuration.allowedMethods =
            mutableListOf("HEAD", "GET", "POST", "PUT", "PATCH", "DELETE")
        configuration.addAllowedHeader("*")
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)

        return source
    }

    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder? {
        return BCryptPasswordEncoder()
    }
}
