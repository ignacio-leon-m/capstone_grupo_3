package org.duocuc.capstone_backend.ai

import com.fasterxml.jackson.databind.JsonNode
import org.duocuc.capstone_backend.model.Tema
import org.duocuc.capstone_backend.repository.DocumentoRepository
import org.duocuc.capstone_backend.repository.TemaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class TemaAiService(
    private val temaRepo: TemaRepository,
    private val docRepo: DocumentoRepository,
    private val extractor: TextExtractor,
    private val gemini: GeminiService
) {

    @Transactional
    fun generarResumenYQuiz(temaId: UUID): GeneracionAiResultado {
        val tema: Tema = temaRepo.findById(temaId)
            .orElseThrow { IllegalArgumentException("Tema no encontrado: $temaId") }

        // Toma el primer documento del tema (puedes cambiar la estrategia si hay varios)
        val doc = docRepo.findByTemaId(temaId).firstOrNull()
            ?: throw IllegalStateException("El tema no tiene documentos para analizar")

        // 1) Extraer texto del PDF/DOCX
        val texto = extractor.extract(doc.storagePath)

        // 2) Generar resumen y quiz con Gemini
        val resumen: String = gemini.resumen(texto)
        val quizJson: JsonNode = gemini.quiz(texto)

        // 3) Guardar en el tema
        tema.resumen = resumen
        tema.quiz = quizJson   // ⬅️ si tu Tema.quiz es JsonNode?
        // Si en tu entidad Tema.quiz lo dejaste como String? con @JdbcTypeCode(JSON):
        // tema.quiz = quizJson.toString()

        temaRepo.save(tema)

        return GeneracionAiResultado(
            temaId = tema.id!!,
            resumen = resumen,
            quiz = quizJson.toString()
        )
    }
}

data class GeneracionAiResultado(
    val temaId: UUID,
    val resumen: String,
    val quiz: String // lo devolvemos como texto para inspección rápida
)
