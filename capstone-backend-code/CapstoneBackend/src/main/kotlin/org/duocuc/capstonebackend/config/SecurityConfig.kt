package org.duocuc.capstonebackend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        "/",
                        "/index.html",
                        "/home.html",
                        "/user.html",
                        "/user-upload.html",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/api/auth/**",
                        "/api/users/**",
                        "/download/**" 
                    ).permitAll()
                    .anyRequest().authenticated()
            }
        return http.build()
    }
}