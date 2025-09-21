package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.dto.RegisterRequestDto
import org.duocuc.capstonebackend.service.AuthService
import org.duocuc.capstonebackend.service.FileUploadService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.FileInputStream

@RestController
@RequestMapping("/api/files")
class FileUploadController(
    private val fileUploadService: FileUploadService,
    private val authService: AuthService
) {
    @PostMapping("/register-from-excel")
    fun uploadExcelFile(@RequestParam("file") file: MultipartFile): ResponseEntity<String> {
        return try {
            val students = file.inputStream.use { inputStream ->
                fileUploadService.processExcelFile(inputStream)
            }
            students.forEach {
                authService.userRegistry(it)
            }
            ResponseEntity("Usuarios creados exitosamente", HttpStatus.CREATED)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar el archivo Excel: ${e.message}")
        }
    }
}