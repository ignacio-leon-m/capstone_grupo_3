package org.duocuc.capstonebackend.service

import org.springframework.stereotype.Service

@Service
class PdfProcessingService(
    private val persistingAiService: PersistenceAiService
) {
    14  private val log = LoggerFactory.getLogger(PdfProcessingService::class.java)
    15
    16  @Async
    17         fun processAndSavePdf(fileBytes: ByteArray, mimeType: String, title: String) {
        18             try {
            19                 log.info("Iniciando procesamiento asíncrono para el archivo: {}", title)
            20
            21                 // Llama a tu servicio existente. Este ya usa Gemini y persiste en Mongo.
            22                 // Aquí asumimos que quieres un resumen. Si es un quiz, llama al otro método.
            23                 val summaryDto = persistingAiService.summarizeDocument(fileBytes, mimeType, title)
            24
            25                 log.info("Procesamiento completado para: {}. Resumen: {}", title, summaryDto.summary)
            26                 // La persistencia ya ocurrió dentro de persistingAiService
            27
            28             } catch (e: Exception) {
            29                 log.error("Error en el procesamiento asíncrono del PDF: {}", title, e)
            30                 // Aquí podrías actualizar el estado de la tarea a 'ERROR'
            31             }
        32         }