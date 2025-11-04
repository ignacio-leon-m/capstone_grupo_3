package org.duocuc.capstonebackend.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig: WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            // 💡 SOLUCIÓN: Reemplaza "*" por la IP específica de tu notebook y localhost
            .allowedOrigins("http://localhost:8080", "http://192.168.1.14:8080")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true) // Ahora funciona porque los orígenes son explícitos
    }
}
