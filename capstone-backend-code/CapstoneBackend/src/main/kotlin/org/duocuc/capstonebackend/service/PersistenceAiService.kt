package org.duocuc.capstonebackend.service

import org.duocuc.capstonebackend.nosql.AiQueryLog
import org.duocuc.capstonebackend.nosql.AiQueryLogRepository
import org.duocuc.capstonebackend.security.CustomUserDetails
import org.duocuc.capstonebackend.util.HashingUtils
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

/**
 * Decorador de persistencia (Mongo) para AiService.
 * Loguea todas las consultas a la IA.
 */
@Service("persistingAiService")
class PersistenceAiService(
    @param:Qualifier("cachingAiService") private val delegate: AiService,
    private val queryLogRepo: AiQueryLogRepository,
) : AiService {

    override fun query(text: String, prompt: String): String {
        val response = delegate.query(text, prompt)
        val auth = SecurityContextHolder.getContext().authentication
        val user = auth?.principal as? CustomUserDetails
        if (user != null) {
            val textSha1 = HashingUtils.sha256Hex(text)

            queryLogRepo.save(
                AiQueryLog(
                    userId = user.id,
                    textSha1 = textSha1,
                    prompt = prompt,
                    response = response
                )
            )
        }
        return response
    }
}
