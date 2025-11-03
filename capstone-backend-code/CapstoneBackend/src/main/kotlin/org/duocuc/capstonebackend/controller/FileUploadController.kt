package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.service.AuthService
import org.duocuc.capstonebackend.service.FileUploadService
import org.duocuc.capstonebackend.service.PdfProcessingService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("/api/files")
class FileUploadController(
    private val fileUploadService: FileUploadService,
    private val authService: AuthService,
    private val pdfProcessingService: PdfProcessingService
) {
    private val log = LoggerFactory.getLogger(FileUploadController::class.java)

    @PostMapping("/upload-excel")
    @PreAuthorize("hasAuthority('profesor')")
    fun uploadExcelFile(@RequestParam("file") file: MultipartFile): ResponseEntity<String> {
        return try {
            val students = file.inputStream.use { inputStream ->
                fileUploadService.processExcelFile(inputStream)
            }
            log.info("El servicio de Excel procesó ${students.size} alumnos.")

            students.forEach {
                authService.registerStudentFromExcel(it)
            }
            ResponseEntity("Archivo procesado. Los alumnos han sido creados o actualizados.", HttpStatus.CREATED)
        } catch (e: Exception) {
            log.error("Error al procesar el archivo Excel", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar el archivo Excel: ${e.message}")
        }
    }

    @PostMapping("/upload-pdf")
    fun uploadPdf(@RequestParam("title") title: String, @RequestParam("file") file: MultipartFile): ResponseEntity<Map<String, String>> {
        if (file.isEmpty || !file.contentType.equals("application/pdf") ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to "Archivo inválido. Se requiere un archivo PDF válido."))
        }
        val taskId = UUID.randomUUID().toString()
        pdfProcessingService.processAndSavePdf(file.bytes, file.contentType!!, title)
        return ResponseEntity.accepted().body(mapOf("taskId" to taskId, "status" to "Procesamiento iniciado"))
    }

}