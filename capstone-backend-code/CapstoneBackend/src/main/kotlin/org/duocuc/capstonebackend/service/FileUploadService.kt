package org.duocuc.capstonebackend.service

import org.apache.poi.ss.usermodel.WorkbookFactory
import org.duocuc.capstonebackend.dto.RegisterRequestDto
import org.springframework.stereotype.Service
import org.duocuc.capstonebackend.util.nameToTitleCase
import org.duocuc.capstonebackend.util.splitFullNameFromExcel
import java.io.InputStream

@Service
class FileUploadService {
    fun processExcelFile(excelInputStream: InputStream): List<RegisterRequestDto> {
        val workbook = WorkbookFactory.create(excelInputStream)
        val sheet = workbook.getSheetAt(0)
        val students = mutableListOf<RegisterRequestDto>()

        for (row in sheet.drop(11)) {
            val rut = row.getCell(1)?.toString()?.trim()
            if (rut.isNullOrBlank()) {
                break
            }

            val fullName = row.getCell(2)?.toString()?.trim()
            if (fullName.isNullOrBlank()) {
                continue
            }

            val fullNameToTitleCase = fullName.nameToTitleCase()
            val (lastName, firstName) = splitFullNameFromExcel(fullNameToTitleCase)

            val degreeName = "Ingeniería en Informática"



            students.add(RegisterRequestDto(
                name = firstName,
                lastName = lastName,
                email = "$rut@duocuc.cl",
                phone = "",
                password = rut
                    .replace(".", "")
                    .replace("-", "")
                    .takeLast(4) + "1234",
                role = "alumno",
                degreeName = degreeName,
            ))
        }
        return students
    }
}