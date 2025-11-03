package org.duocuc.capstonebackend.service

import org.apache.poi.ss.usermodel.WorkbookFactory
import org.duocuc.capstonebackend.dto.RegisterRequestDto
import org.duocuc.capstonebackend.nosql.DocumentAnalysis
import org.duocuc.capstonebackend.repository.DocumentAnalysisRepository
import org.duocuc.capstonebackend.service.impl.GeminiService
import org.springframework.stereotype.Service
import org.duocuc.capstonebackend.util.nameToTitleCase
import org.duocuc.capstonebackend.util.splitFullNameFromExcel
import java.io.InputStream

@Service
class FileUploadService (
    private val repository: DocumentAnalysisRepository,
    private val geminiService: GeminiService
){
    fun processExcelFile(excelInputStream: InputStream): List<RegisterRequestDto> {
        val workbook = WorkbookFactory.create(excelInputStream)
        val sheet = workbook.getSheetAt(0)
        val degreeName = sheet.getRow(5)?.getCell(2)?.toString()?.trim()
            ?: throw IllegalStateException("El formato del archivo es incorrecto.")
        val students = mutableListOf<RegisterRequestDto>()

        for (row in sheet.drop(11)) {
            val rut = row.getCell(1)?.toString()?.trim()
            if (rut.isNullOrBlank()) {
                continue
            }
            val fullName = row.getCell(2)?.toString()?.trim()
            if (fullName.isNullOrBlank()) {
                continue
            }
            val fullNameToTitleCase = fullName.nameToTitleCase()
            val (lastName, firstName) = splitFullNameFromExcel(fullNameToTitleCase)

            students.add(RegisterRequestDto(
                name = firstName,
                lastName = lastName,
                rut = rut,
                email = "$rut@duocuc.cl",
                phone = "",
                password = rut
                    .replace(".", "")
                    .replace("-", "")
                    .takeLast(4) + "1234",
                role = "alumno"
            ))
        }
        return students
    }
}