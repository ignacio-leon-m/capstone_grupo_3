package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.dto.AiQuizDto
import org.duocuc.capstonebackend.dto.AiSummaryDto
import org.duocuc.capstonebackend.dto.SaveQuizRequestDto
import org.duocuc.capstonebackend.service.AiService
import org.duocuc.capstonebackend.service.QuizPersistService
import org.duocuc.capstonebackend.util.PdfTextExtractor
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("/api/ai/pdf")
class AiPdfController(
    // IA + persistencia en Mongo
    @Qualifier("persistingAiService") private val aiService: AiService,
    // Persistencia de preguntas en Postgres
    private val quizPersistService: QuizPersistService
) {

    companion object {
        private val log = LoggerFactory.getLogger(AiPdfController::class.java)
    }

    /**
     * Resumen (3 líneas). El documento y el resumen se guardan en Mongo
     * vía PersistingAiService.
     */
    @PostMapping(
        "/resumen",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("hasAnyAuthority('profesor','admin')")
    fun resumir(
        @RequestParam("file") file: MultipartFile
    ): AiSummaryDto {
        val bytes = file.bytes
        val mime = file.contentType ?: "application/pdf"
        val title = file.originalFilename ?: "documento.pdf"
        return aiService.summarizeDocument(bytes, mime, title)
    }

    /**
     * Genera un quiz desde PDF (NO persiste en Postgres).
     * El quiz generado se deja en Mongo como historial (PersistingAiService).
     */
    @PostMapping(
        "/quiz",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("hasAnyAuthority('profesor','admin')")
    fun quiz(
        @RequestParam("file") file: MultipartFile,
        @RequestParam(name = "numQuestions", defaultValue = "5") numQuestions: Int
    ): AiQuizDto {
        val bytes = file.bytes
        val mime = file.contentType ?: "application/pdf"
        val text = PdfTextExtractor.safeExtract(bytes, mime)
        return aiService.generateQuizFromText(text, numQuestions)
    }

    /**
     * Genera un quiz desde PDF y lo PERSISTE en Postgres en:
     *  - temas (si no existe)
     *  - preguntas (siempre inserta nuevas)
     * Además, el historial del quiz queda en Mongo (PersistingAiService).
     */
    @PostMapping(
        "/quiz/persist",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("hasAnyAuthority('profesor','admin')")
    fun quizPersist(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("asignaturaId") asignaturaId: UUID,
        @RequestParam(name = "tema", defaultValue = "Generado IA") tema: String,
        @RequestParam(name = "numQuestions", defaultValue = "5") numQuestions: Int
    ): Map<String, Any> {

        // Útil para depurar 403/roles
        val auth = SecurityContextHolder.getContext().authentication
        log.info("Auth principal={}, authorities={}", auth?.name, auth?.authorities)

        val text = PdfTextExtractor.safeExtract(file.bytes, file.contentType ?: "application/pdf")
        val quiz = aiService.generateQuizFromText(text, numQuestions)

        val saved = quizPersistService.saveQuiz(
            SaveQuizRequestDto(
                subjectId = asignaturaId,
                topicName = tema,
                questions = quiz.questions
            )
        )

        return mapOf(
            "ok" to true,
            "inserted" to saved.saved,
            "asignaturaId" to asignaturaId,
            "tema" to tema
        )
    }
}
