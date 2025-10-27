package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.dto.AiQuizDto
import org.duocuc.capstonebackend.dto.AiSummaryDto
import org.duocuc.capstonebackend.service.AiService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/ai")
class AiController(
    @Qualifier("persistingAiService")
    private val aiService: AiService
) {
    @PostMapping(
        "/summarize",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("hasAnyAuthority('admin','profesor')")
    fun summarize(
        @RequestPart("file") file: MultipartFile,
        @RequestPart("title", required = false) title: String?
    ): AiSummaryDto {
        val bytes = file.bytes
        val mime = file.contentType ?: "application/octet-stream"
        val t = title ?: file.originalFilename.orEmpty()
        return aiService.summarizeDocument(bytes, mime, t)
    }

    data class QuizRequest(val text: String, val numQuestions: Int? = 5)

    @PostMapping(
        "/quiz",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("hasAnyAuthority('admin','profesor')")
    fun quiz(@RequestBody body: QuizRequest): AiQuizDto {
        return aiService.generateQuizFromText(body.text, body.numQuestions ?: 5)
    }
}
