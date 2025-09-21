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
                    .requestMatchers("/api/auth/**").permitAll() // Authorize access to /users endpoint
                    .requestMatchers("/", "/index.html", "/css/**", "/images/**").permitAll()
                    .requestMatchers("/", "/home.html", "/css/**", "/images/**").permitAll()
                    .requestMatchers("/", "/user.html", "/css/**", "/images/**","/js/**").permitAll()
                    .requestMatchers("/", "/user-upload.html", "/css/**", "/images/**","/js/**").permitAll()
                    .requestMatchers("/", "/content.html", "/css/**", "/images/**","/js/**").permitAll()
                    .requestMatchers("/", "/content-upload.html", "/css/**", "/images/**","/js/**").permitAll()

                    .anyRequest().authenticated() // the rest of endpoints require authentication
            }
        return http.build()
    }
}