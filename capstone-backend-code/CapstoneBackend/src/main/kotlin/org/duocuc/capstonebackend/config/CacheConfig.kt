package org.duocuc.capstonebackend.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
class CacheConfig {

    @Bean
    fun caffeineSpec(
        @Value("\${AI_CACHE_MAXSIZE:2000}") maxSize: Long,
        @Value("\${AI_CACHE_TTL_SECONDS:1800}") ttlSeconds: Long
    ): Caffeine<Any, Any> =
        Caffeine.newBuilder()
            .maximumSize(maxSize)
            .expireAfterWrite(ttlSeconds, TimeUnit.SECONDS)

    @Bean
    fun cacheManager(caffeine: Caffeine<Any, Any>): CacheManager {
        val cm = CaffeineCacheManager("ai_summaries", "ai_quizzes")
        cm.setCaffeine(caffeine)
        return cm
    }
}
