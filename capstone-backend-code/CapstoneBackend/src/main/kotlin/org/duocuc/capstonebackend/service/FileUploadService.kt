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
            val rutCell = row.getCell(1)
            if (rutCell.toString().isBlank()) {
                break
            }
            val fullName = row.getCell(2)?.stringCellValue?: continue
            val fullNameToTitleCase = fullName.nameToTitleCase()
            val (lastName, firstName) = splitFullNameFromExcel(fullNameToTitleCase)
            students.add(RegisterRequestDto(
                name = firstName,
                lastName = lastName,
                email = "${rutCell}@duocuc.cl",
                phone = "",
                password = rutCell.toString()
                    .replace("-", "")
                    .takeLast(4) + "1234",
                role = "alumno",
                degreeName = "carrera-default"
            ))
        }
        return students
    }


}
