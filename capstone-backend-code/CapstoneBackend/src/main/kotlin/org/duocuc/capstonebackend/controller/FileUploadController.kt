package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.dto.UserResponseDto
import org.duocuc.capstonebackend.service.AuthService
import org.duocuc.capstonebackend.service.FileUploadService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/files")
class FileUploadController(
    private val fileUploadService: FileUploadService,
    private val authService: AuthService
) {
    @PostMapping
    fun uploadExcelFile(request: MultipartFile): ResponseEntity<List<UserResponseDto>> {
        val students = fileUploadService.processExcelFile(request)
        val registeredUsers = students.map { authService.userRegistry(it) }
        return ResponseEntity.ok(registeredUsers)
    }
}