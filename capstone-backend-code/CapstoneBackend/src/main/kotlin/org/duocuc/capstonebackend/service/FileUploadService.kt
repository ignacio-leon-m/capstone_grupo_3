package org.duocuc.capstonebackend.service

import org.duocuc.capstonebackend.dto.RegisterRequestDto
import org.springframework.web.multipart.MultipartFile
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.duocuc.capstonebackend.dto.UserResponseDto
import org.springframework.stereotype.Service

@Service
class FileUploadService(
    private val authService: AuthService
) {
    fun processExcelFile(file: MultipartFile): List<RegisterRequestDto> {
        val students = mutableListOf<RegisterRequestDto>()
        file.inputStream.use { input ->
            val workbook = XSSFWorkbook(input)
            val sheet = workbook.getSheetAt(0) // first sheet
            for (row in sheet.drop(1)) { // drop(1) to skip header row
                val name = row.getCell(0)?.stringCellValue ?: ""
                val lastName = row.getCell(1)?.stringCellValue ?: ""
                val email = row.getCell(2)?.stringCellValue ?: ""
                val phone = row.getCell(3)?.stringCellValue ?: ""
                val password = name.lowercase() + "1234" // Default password
                students.add(RegisterRequestDto(name, lastName, email, phone, password))
            }
        }
        return students
    }
}