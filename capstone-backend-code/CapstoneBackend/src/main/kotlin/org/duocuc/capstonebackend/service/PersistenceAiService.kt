package org.duocuc.capstonebackend.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.duocuc.capstonebackend.dto.AiQuizDto
import org.duocuc.capstonebackend.dto.AiSummaryDto
import org.duocuc.capstonebackend.nosql.AiQuizDoc
import org.duocuc.capstonebackend.nosql.AiQuizRepository
import org.duocuc.capstonebackend.nosql.AiSummaryDoc
import org.duocuc.capstonebackend.nosql.AiSummaryRepository
import org.duocuc.capstonebackend.security.CurrentUser
import org.duocuc.capstonebackend.service.AiService
import org.duocuc.capstonebackend.util.Checksums
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

/**
 * Decorador de persistencia (Mongo) para AiService.
 * Persiste summaries y quizzes generados, y luego retorna el DTO original.
 */
@Service("persistingAiService")
class PersistenceAiService(
    @Qualifier("geminiService") private val delegate: AiService,
    private val summaryRepo: AiSummaryRepository,
    private val quizRepo: AiQuizRepository,
    private val currentUser: CurrentUser,
    private val mapper: ObjectMapper = ObjectMapper()
) : AiService {

    override fun summarizeDocument(bytes: ByteArray, mimeType: String, title: String): AiSummaryDto {
        val dto = delegate.summarizeDocument(bytes, mimeType, title)

        val userId = currentUser.id()
        val sha1 = Checksums.sha1(bytes)

        summaryRepo.save(
            AiSummaryDoc(
                userId = userId,
                title = title,
                mimeType = mimeType,
                sizeBytes = bytes.size.toLong(),
                sha1 = sha1,
                summary = dto.summary
            )
        )
        return dto
    }

    override fun generateQuizFromText(text: String, numQuestions: Int): AiQuizDto {
        val quiz = delegate.generateQuizFromText(text, numQuestions)

        val userId = currentUser.id()
        quizRepo.save(
            AiQuizDoc(
                userId = userId,
                sourceTitle = "Texto libre",
                sourceSha1 = null,
                numQuestions = quiz.questions.size,
                payload = mapper.writeValueAsString(quiz)
            )
        )

        return quiz
    }
}
