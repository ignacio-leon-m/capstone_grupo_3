package org.duocuc.capstonebackend.service

import org.duocuc.capstonebackend.util.HashingUtils
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Service


@Service("cachingAiService")
class CachingAiService(
    @param:Qualifier("geminiService") private val delegate: AiService,
    private val cacheManager: CacheManager
) : AiService {

    override fun query(text: String, prompt: String): String {
        val key = "query:${HashingUtils.sha256Hex(text)}:${HashingUtils.sha256Hex(prompt)}"
        val cache = cacheManager.getCache("ai_queries")
        cache?.get(key, String::class.java)?.let { return it }

        val result = delegate.query(text, prompt)
        cache?.put(key, result)
        return result
    }
}
