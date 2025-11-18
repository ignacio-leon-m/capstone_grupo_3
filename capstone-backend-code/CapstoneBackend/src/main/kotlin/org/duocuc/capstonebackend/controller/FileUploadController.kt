package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.service.AiService
import org.duocuc.capstonebackend.service.AuthService
import org.duocuc.capstonebackend.service.FileUploadService
import org.duocuc.capstonebackend.util.PdfTextExtractor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/files")
class FileUploadController(
    private val fileUploadService: FileUploadService,
    private val authService: AuthService,
    @param:Qualifier("persistingAiService") private val aiService: AiService
) {
    private val log = LoggerFactory.getLogger(FileUploadController::class.java)

    // Upload and create users from an Excel file
    @PostMapping("/upload-excel")
    @PreAuthorize("hasAuthority('profesor')")
    fun uploadExcelFile(@RequestParam("file") file: MultipartFile): ResponseEntity<String> {
        return try {
            val students = file.inputStream.use { inputStream ->
                fileUploadService.processExcelFile(inputStream)
            }
            if (students.isEmpty()) {
                log.warn("El archivo Excel no contiene alumnos válidos para procesar.")
                return ResponseEntity.badRequest()
                    .body("El archivo no contiene alumnos válidos. Verifica el formato del Excel.")
            }
            log.info("El servicio de Excel procesó ${students.size} alumnos del archivo.")
            var created = 0
            var skipped = 0
            var errors = 0
            students.forEach { student ->
                try {
                    val wasCreated = authService.registerStudentFromExcel(student)
                    if (wasCreated) created++ else skipped++
                } catch (e: Exception) {
                    log.error("Error al registrar alumno ${student.email}: ${e.message}")
                    errors++
                }
            }
            val resultMessage = buildString {
                append("Archivo procesado: ")
                append("${students.size} alumnos encontrados. ")
                append("Creados: $created, ")
                append("Ya existían: $skipped")
                if (errors > 0) {
                    append(", Errores: $errors")
                }
            }
            log.info(resultMessage)
            ResponseEntity(resultMessage, HttpStatus.CREATED)
        } catch (e: Exception) {
            log.error("Error al procesar el archivo Excel", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al procesar el archivo Excel: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al procesar el archivo Excel: ${e.message}")
        }
    }

    // Upload PDF Content
    @PostMapping(
        "/upload-query-pdf",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("hasAnyAuthority('profesor','admin')")
    fun processPdfFileAndQuery(
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