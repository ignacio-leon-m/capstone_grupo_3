package org.duocuc.capstonebackend.service

import org.apache.poi.ss.usermodel.WorkbookFactory
import org.duocuc.capstonebackend.dto.RegisterRequestDto
import org.springframework.stereotype.Service
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
            val (lastName, firstName) = splitFullNameFromExcel(fullName)
            students.add(RegisterRequestDto(
                name = firstName,
                lastName = lastName,
                email = "${rutCell}@duocuc.cl",
                phone = "",
                password = rutCell.toString()
                    .replace("-", "")
                    .takeLast(4) + "1234"
            ))
        }
        return students
    }

    fun splitFullNameFromExcel(fullName: String): Pair<String, String> {
        val parts = fullName.trim().split(" ")
        return if (parts.size >= 3) {
            val lastName = parts.take(2).joinToString(" ")
            val firstName =  parts.drop(2).joinToString(" ")
            Pair(lastName, firstName)
        } else {
            // if there are just two parts, one is lastName and the other is firstName
            Pair(parts.firstOrNull()?: "", parts.getOrNull(1)?: "")
        }
    }
}
