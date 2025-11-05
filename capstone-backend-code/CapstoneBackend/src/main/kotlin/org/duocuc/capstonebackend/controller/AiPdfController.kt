package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.service.AiService
import org.duocuc.capstonebackend.service.FileUploadService
import org.duocuc.capstonebackend.util.PdfTextExtractor
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/ai/pdf")
class AiPdfController(
    @param:Qualifier("persistingAiService") private val aiService: AiService,
    private val fileUploadService: FileUploadService
) {

    @PostMapping(
        "/query",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("hasAnyAuthority('profesor','admin')")
    fun query(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("prompt") prompt: String
    ): Map<String, Any> {
        val storedDocument = fileUploadService.savePdf(file)
        val text = PdfTextExtractor.safeExtract(file.bytes)

        val response = aiService.query(text, prompt)

        return mapOf(
            "documentId" to storedDocument.id,
            "fileName" to storedDocument.fileName,
            "prompt" to prompt,
            "response" to response
        )
    }
}
