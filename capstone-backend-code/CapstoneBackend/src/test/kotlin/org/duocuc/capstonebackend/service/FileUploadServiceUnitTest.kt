package org.duocuc.capstonebackend.service

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class FileUploadServiceUnitTest {

    private val service = FileUploadService(
        storedDocumentRepository = TODO(),
        currentUser = TODO()
    )

    //@Test
    fun `processExcelFile successfully processes a valid file`() {
        // Arrange: Crear un Excel falso en memoria
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Alumnos")

        // Escribir el encabezado (la carrera en la celda C6)
        sheet.createRow(5).createCell(2).setCellValue("Ingeniería en Informática")

        // Escribir las cabeceras de la tabla de alumnos (fila 11)
        val headerRow = sheet.createRow(10)
        headerRow.createCell(1).setCellValue("RUT")
        headerRow.createCell(2).setCellValue("Nombre Completo")

        // Escribir datos de alumnos
        val row1 = sheet.createRow(11)
        row1.createCell(1).setCellValue("11.111.111-1")
        row1.createCell(2).setCellValue("Jagger, Mick")

        val row2 = sheet.createRow(12)
        row2.createCell(1).setCellValue("22.222.222-2")
        row2.createCell(2).setCellValue("Richards, Keith")
        
        // Convertir el workbook a un InputStream
        val outputStream = ByteArrayOutputStream()
        workbook.write(outputStream)
        val inputStream = ByteArrayInputStream(outputStream.toByteArray())

        // Act: Procesar el archivo
        val result = service.processExcelFile(inputStream)

        // Assert: Verificar los resultados
        assertEquals(2, result.size)

        // Verificar Alumno 1
        val student1 = result[0]
        assertEquals("Mick", student1.name)
        assertEquals("Jagger", student1.lastName)
        assertEquals("11.111.111-1", student1.rut)
        assertEquals("11.111.111-1@duocuc.cl", student1.email)
        assertEquals("11111234", student1.password) // Últimos 4 del RUT + 1234
        assertEquals("alumno", student1.role)
        assertEquals("Ingeniería en Informática", student1.name) // Carrera del encabezado

        // Verificar Alumno 2
        val student2 = result[1]
        assertEquals("Keith", student2.name)
        assertEquals("Richards", student2.lastName)
        assertEquals("22.222.222-2", student2.rut)
        assertEquals("22.222.222-2@duocuc.cl", student2.email)
        assertEquals("22221234", student2.password)
        assertEquals("alumno", student2.role)
        assertEquals("Ingeniería en Informática", student2.name) // Carrera del encabezado
    }

    //@Test
    fun `processExcelFile throws exception if degree name is missing`() {
        // Arrange: Crear un Excel sin la carrera en la celda C6
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Alumnos")
        sheet.createRow(11) // Crear una fila de para que el procesamiento comience

        val outputStream = ByteArrayOutputStream()
        workbook.write(outputStream)
        val inputStream = ByteArrayInputStream(outputStream.toByteArray())

        // Act & Assert
        val exception = assertThrows<IllegalStateException> {
            service.processExcelFile(inputStream)
        }
        assertEquals("No se pudo encontrar el nombre de la carrera en la celda C6 del archivo Excel. El formato del archivo es incorrecto.", exception.message)
    }

    //@Test
    fun `processExcelFile ignores empty rows`() {
        // Arrange: Crear un Excel con filas vacías entre alumnos
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Alumnos")
        sheet.createRow(5).createCell(2).setCellValue("Ingeniería en Informática")
        sheet.createRow(10) // Cabecera

        // Alumno 1
        val row1 = sheet.createRow(11)
        row1.createCell(1).setCellValue("11.111.111-1")
        row1.createCell(2).setCellValue("Jagger, Mick")

        // Fila vacía
        sheet.createRow(12)

        // Alumno 2
        val row3 = sheet.createRow(13)
        row3.createCell(1).setCellValue("22.222.222-2")
        row3.createCell(2).setCellValue("Richards, Keith")

        val outputStream = ByteArrayOutputStream()
        workbook.write(outputStream)
        val inputStream = ByteArrayInputStream(outputStream.toByteArray())

        // Act
        val result = service.processExcelFile(inputStream)

        // Assert: Debe procesar solo 2 alumnos, ignorando la fila vacía
        assertEquals(2, result.size)
    }
}