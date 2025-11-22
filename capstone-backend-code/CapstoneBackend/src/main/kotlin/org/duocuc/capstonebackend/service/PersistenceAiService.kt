package org.duocuc.capstonebackend.service

import org.duocuc.capstonebackend.nosql.AiQueryLog
import org.duocuc.capstonebackend.nosql.AiQueryLogRepository
import org.duocuc.capstonebackend.security.CurrentUser
import org.duocuc.capstonebackend.util.Hashing
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

/**
 * Decorador de persistencia (Mongo) para AiService.
 * Loguea todas las consultas a la IA.
 */
@Service("persistingAiService")
class PersistenceAiService(
    @param:Qualifier("cachingAiService") private val delegate: AiService,
    @param:Qualifier("currentUser") private val currentUser: CurrentUser,
    private val queryLogRepo: AiQueryLogRepository,
) : AiService {

    override fun query(text: String, prompt: String): String {
        val response = delegate.query(text, prompt)

        val userId = currentUser.id()
        val textSha1 = Hashing.sha256Hex(text)

        queryLogRepo.save(
            AiQueryLog(
                userId = userId,
                textSha1 = textSha1,
                prompt = prompt,
                response = response
            )
        )

        return response
    }
}
