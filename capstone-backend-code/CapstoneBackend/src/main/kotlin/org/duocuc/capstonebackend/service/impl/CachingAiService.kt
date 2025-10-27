package org.duocuc.capstonebackend.service.impl

import org.duocuc.capstonebackend.dto.AiQuizDto
import org.duocuc.capstonebackend.dto.AiSummaryDto
import org.duocuc.capstonebackend.service.AiService
import org.duocuc.capstonebackend.util.Hashing
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

/**
 * Decorador de caché para AiService.
 * Mantiene la arquitectura: los controllers siguen inyectando AiService.
 */
@Service
@Primary
class CachingAiService(
    @param:Qualifier("geminiService") private val delegate: AiService,
    private val cacheManager: CacheManager
) : AiService {

    override fun summarizeDocument(bytes: ByteArray, mimeType: String, title: String): AiSummaryDto {
        val key = "sum:${Hashing.sha256Hex(bytes)}:$mimeType"
        val cache = cacheManager.getCache("ai_summaries")
        cache?.get(key, AiSummaryDto::class.java)?.let { return it }

        val result = delegate.summarizeDocument(bytes, mimeType, title)
        cache?.put(key, result)
        return result
    }

    override fun generateQuizFromText(text: String, numQuestions: Int): AiQuizDto {
        val nq = numQuestions.coerceIn(3, 10)
        val key = "quiz:${Hashing.sha256Hex(text)}:$nq"
        val cache = cacheManager.getCache("ai_quizzes")
        cache?.get(key, AiQuizDto::class.java)?.let { return it }

        val result = delegate.generateQuizFromText(text, nq)
        cache?.put(key, result)
        return result
    }
}
