package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.dto.AiQuizDto
import org.duocuc.capstonebackend.dto.AiSummaryDto
import org.duocuc.capstonebackend.service.AiService
import org.duocuc.capstonebackend.util.PdfTextExtractor
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/ai/pdf")
class AiPdfController(
    private val aiService: AiService
) {

    // Resumen de 3 líneas desde un PDF
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

    // Quiz (N preguntas) desde un PDF
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
}
