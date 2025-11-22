package org.duocuc.capstonebackend.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.duocuc.capstonebackend.dto.ConceptCreateDto
import org.duocuc.capstonebackend.model.Question
import org.duocuc.capstonebackend.repository.*
import org.duocuc.capstonebackend.service.AiService
import org.duocuc.capstonebackend.service.FileUploadService
import org.duocuc.capstonebackend.util.PdfTextExtractor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*
import org.duocuc.capstonebackend.service.ConceptService

@RestController
@RequestMapping("/api/files/pdf")
class AiPdfController(
    @Qualifier("persistingAiService") private val aiService: AiService,
    private val fileUploadService: FileUploadService,
    private val conceptService: ConceptService,
    private val questionRepository: QuestionRepository,
    private val subjectRepository: SubjectRepository,
    private val topicRepository: TopicRepository
) {

    companion object {
        private val log = LoggerFactory.getLogger(AiPdfController::class.java)
    }

    @PostMapping("/query", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @PreAuthorize("hasAnyAuthority('profesor','admin')")
    fun queryPdf(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("prompt") prompt: String
    ): Map<String, Any> {
        val stored = fileUploadService.savePdf(file)
        val text = PdfTextExtractor.safeExtract(file.bytes)
        val aiResponse = aiService.query(text, prompt)

        return mapOf(
            "documentId" to stored.id,
            "fileName" to stored.fileName,
            "prompt" to prompt,
            "response" to aiResponse
        )
    }

    @PostMapping("/concepts", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @PreAuthorize("hasAnyAuthority('profesor','admin')")
    fun extractConceptsAndSave(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("topicId") topicId: UUID,
        @RequestParam(name = "max", defaultValue = "30") max: Int
    ): ResponseEntity<Any> {
        try {
            val text = PdfTextExtractor.safeExtract(file.bytes)

            if (text.length < 10) {
                return ResponseEntity.badRequest().body(mapOf("inserted" to 0))
            }

            if (!topicRepository.existsById(topicId)) {
                return ResponseEntity.badRequest().body(mapOf("inserted" to 0))
            }

            val prompt = """
                EXTRACCIÓN DE CONCEPTOS ACADÉMICOS
                INSTRUCCIONES:
                1. Analiza el texto y extrae conceptos académicos importantes
                2. Devuelve SOLO un array JSON válido
                3. NO uses markdown

                FORMATO:
                [
                  {"word": "CONCEPTO", "hint": "Definición"}
                ]

                TEXTO:
                $text
            """.trimIndent()

            val raw = aiService.query(text, prompt)
            val cleaned = cleanAiResponse(raw)

            val mapper = jacksonObjectMapper()
            val nodes = mapper.readTree(cleaned)
            val concepts = nodes.mapNotNull { node ->
                val word = node.get("word")?.asText()?.trim()?.uppercase()
                val hint = node.get("hint")?.asText()?.trim()
                if (word != null && word.length in 3..30) {
                    ConceptCreateDto(word, hint, topicId)
                } else null
            }

            if (concepts.isEmpty()) {
                return ResponseEntity.badRequest().body(mapOf("inserted" to 0))
            }

            val saved = conceptService.createConcepts(topicId, concepts)

            return ResponseEntity.ok(mapOf(
                "inserted" to saved.size,
                "examples" to saved.take(5).map { mapOf("word" to it.word, "hint" to it.hint) }
            ))

        } catch (ex: Exception) {
            log.error("Error al generar conceptos", ex)
            return ResponseEntity.internalServerError().body(mapOf("inserted" to 0))
        }
    }

    @PostMapping("/questions", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @PreAuthorize("hasAnyAuthority('profesor','admin')")
    fun extractQuizQuestionsFromPdf(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("subjectId") subjectId: UUID,
        @RequestParam("topicId") topicId: UUID,
        @RequestParam(name = "max", defaultValue = "5") max: Int
    ): ResponseEntity<Map<String, Any>> {
        try {
            val text = PdfTextExtractor.safeExtract(file.bytes)

            if (text.length < 10) {
                return ResponseEntity.badRequest().body(mapOf("message" to "No se pudo extraer contenido válido del PDF."))
            }

            val prompt = """
                GENERACIÓN DE PREGUNTAS TIPO QUIZ
                - Máximo $max preguntas de opción múltiple.
                - Cada pregunta con 4 opciones.
                - Formato JSON únicamente.

                FORMATO:
                [
                  {
                    "question": "Texto?",
                    "options": ["A","B","C","D"],
                    "correctIndex": 1
                  }
                ]

                TEXTO:
                $text
            """.trimIndent()

            val raw = aiService.query(text, prompt)
            val cleaned = cleanAiResponse(raw)

            val mapper = jacksonObjectMapper()
            val nodes = mapper.readTree(cleaned)

            val parsed = nodes.mapNotNull { node ->
                val q = node.get("question")?.asText()?.trim()
                val opts = node.get("options")?.map { it.asText().trim() } ?: emptyList()
                val correctIndex = node.get("correctIndex")?.asInt() ?: -1

                if (q.isNullOrBlank() || opts.size != 4 || correctIndex !in 0..3) null
                else mapOf(
                    "question" to q,
                    "options" to opts,
                    "correctAnswer" to opts[correctIndex]
                )
            }

            val subject = subjectRepository.findById(subjectId).orElse(null)
            val topic = topicRepository.findById(topicId).orElse(null)

            if (subject == null || topic == null) {
                return ResponseEntity.badRequest().body(mapOf("message" to "Asignatura o tema no encontrado."))
            }

            val savedQuestions = parsed.map {
                Question(
                    text = it["question"] as String,
                    correctAnswer = it["correctAnswer"] as String,
                    subject = subject,
                    topic = topic
                )
            }.also { questionRepository.saveAll(it) }

            return ResponseEntity.ok(
                mapOf(
                    "saved" to savedQuestions.size,
                    "questions" to savedQuestions.map {
                        mapOf("text" to it.text, "correctAnswer" to it.correctAnswer)
                    }
                )
            )
        } catch (ex: Exception) {
            log.error("Error generando preguntas", ex)
            return ResponseEntity.internalServerError().body(mapOf("message" to "Error interno generando preguntas."))
        }
    }

    private fun cleanAiResponse(raw: String): String {
        return raw
            .trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
            .let {
                val s = it.indexOf('[')
                val e = it.lastIndexOf(']')
                if (s >= 0 && e > s) it.substring(s, e + 1) else it
            }
            .replace(Regex("""^[^\[]*"""), "")
            .replace(Regex("""[^\]]*\$"""), "")
            .trim()
    }
}
