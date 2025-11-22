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
@RequestMapping("/api/files/pdf")
class AiPdfController(
    @param:Qualifier("persistingAiService") private val aiService: AiService,
    private val fileUploadService: FileUploadService
) {
    
}
