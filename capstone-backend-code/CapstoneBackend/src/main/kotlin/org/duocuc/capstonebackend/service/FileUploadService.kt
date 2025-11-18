package org.duocuc.capstonebackend.service

import org.apache.poi.ss.usermodel.WorkbookFactory
import org.duocuc.capstonebackend.dto.RegisterRequestDto
import org.duocuc.capstonebackend.nosql.StoredDocument
import org.duocuc.capstonebackend.nosql.StoredDocumentRepository
import org.duocuc.capstonebackend.security.CurrentUser
import org.duocuc.capstonebackend.util.HashingUtils
import org.duocuc.capstonebackend.util.nameToTitleCase
import org.duocuc.capstonebackend.util.splitFullNameFromExcel
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

@Service
class FileUploadService(
    private val storedDocumentRepository: StoredDocumentRepository,
    private val currentUser: CurrentUser
) {
    private val uploadDir: Path = Paths.get("uploads")

    init {
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir)
        }
    }

    fun savePdf(file: MultipartFile): StoredDocument {
        val userId = currentUser.id()
        val originalFileName = file.originalFilename ?: "unknown.pdf"
        val fileExtension = originalFileName.substringAfterLast('.', "")
        val uniqueFileName = "${UUID.randomUUID()}.$fileExtension"
        val filePath = uploadDir.resolve(uniqueFileName)

        Files.copy(file.inputStream, filePath)

        val bytes = Files.readAllBytes(filePath)
        val sha1 = HashingUtils.sha1(bytes)

        val storedDocument = StoredDocument(
            userId = userId,
            fileName = originalFileName,
            filePath = filePath.toString(),
            mimeType = file.contentType ?: "application/octet-stream",
            sizeBytes = file.size,
            sha1 = sha1
        )

        return storedDocumentRepository.save(storedDocument)
    }

    fun processExcelFile(excelInputStream: InputStream): List<RegisterRequestDto> {
        val workbook = WorkbookFactory.create(excelInputStream)
        val sheet = workbook.getSheetAt(0)
        // val degreeName = sheet.getRow(5)?.getCell(2)?.toString()?.trim()
        //     ?: throw IllegalStateException("El formato del archivo es incorrecto.")
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

            students.add(
                RegisterRequestDto(
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
                )
            )
        }
        return students
    }
}