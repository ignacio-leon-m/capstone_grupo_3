package org.duocuc.capstonebackend.service.impl

import org.duocuc.capstonebackend.dto.AiQuizDto
import org.duocuc.capstonebackend.dto.AiQuizQuestionDto
import org.duocuc.capstonebackend.dto.AiSummaryDto
import org.duocuc.capstonebackend.service.AiService
import org.springframework.stereotype.Service

@Service
class SimpleAiService : AiService {

    override fun summarizeDocument(bytes: ByteArray, mimeType: String, title: String): AiSummaryDto {
        // Resumen de “placeholder” para pruebas locales.
        val preview = String(bytes, Charsets.UTF_8).take(500).replace("\n", " ")
        val summary = if (preview.isBlank())
            "Resumen no disponible (contenido binario o no-texto)."
        else
            "Resumen automático (demo): $preview..."

        return AiSummaryDto(title = title, summary = summary)
    }

    override fun generateQuizFromText(text: String, numQuestions: Int): AiQuizDto {
        // Genera preguntas dummy a partir de oraciones del texto (para flujos end-to-end).
        val sentences = text
            .split('.', '!', '?', '\n')
            .map { it.trim() }
            .filter { it.length > 10 }

        val qs = (0 until numQuestions).map { i ->
            val base = sentences.getOrElse(i % sentences.size) { "Contenido del documento." }
            val q = "¿Cuál de las siguientes opciones se relaciona con: \"$base\"?"
            val options = listOf(
                "Opción A",
                "Opción B",
                "Opción C",
                "Opción D"
            )
            AiQuizQuestionDto(question = q, options = options, answerIndex = 0)
        }

        return AiQuizDto(questions = qs)
    }
}
