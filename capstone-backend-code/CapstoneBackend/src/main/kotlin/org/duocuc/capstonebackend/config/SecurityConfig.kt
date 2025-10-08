package org.duocuc.capstonebackend.config

import org.duocuc.capstonebackend.service.CustomUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig (
    private val userDetailsService: CustomUserDetailsService
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationProvider(): AuthenticationProvider =
        DaoAuthenticationProvider(userDetailsService).apply {
            setPasswordEncoder(passwordEncoder())
        }

    @Bean
    fun securityFilterChain(http: HttpSecurity, authProvider: AuthenticationProvider): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/", "/index.html", "/login.html", "/css/**", "/js/**", "/images/**").permitAll()
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .requestMatchers("/api/profesor/**").hasRole("PROFESOR")
                    .requestMatchers("/api/estudiante/**").hasRole("ALUMNO") // unificar con path anterior
                    .anyRequest().authenticated()
            }
            .authenticationProvider(authProvider)
        return http.build()
    }
}