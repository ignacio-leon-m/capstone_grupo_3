package org.duocuc.capstonebackend.util

import org.apache.tika.Tika
import org.apache.tika.metadata.Metadata
import org.apache.tika.parser.AutoDetectParser
import org.apache.tika.parser.ParseContext
import org.apache.tika.parser.ocr.TesseractOCRConfig
import org.apache.tika.sax.BodyContentHandler

object PdfTextExtractor {
    private val tika = Tika()
    private val autoParser = AutoDetectParser()
    private val ocrConfig: TesseractOCRConfig = TesseractOCRConfig().apply {
        // Idiomas: español + inglés
        setLanguage("spa+eng")
        // Forzar OCR hasta 20MB (límite actual de subida)
        setMinFileSizeToOcr(0)
        setMaxFileSizeToOcr(20 * 1024 * 1024)
        // Nota: en Tika 3.2.x algunos setters de timeout cambiaron; usamos valores por defecto
    }

    fun safeExtract(bytes: ByteArray): String {
        // 1) Intentar extracción rápida básica (Tika simple parse)
        val quickText = try {
            tika.parseToString(bytes.inputStream()).trim()
        } catch (_: Exception) { "" }
        if (quickText.isNotBlank()) return quickText

        // 2) Intentar extracción completa con OCR (para PDFs escaneados)
        val ocrText = try {
            val handler = BodyContentHandler(-1)
            val metadata = Metadata()
            val context = ParseContext()
            context.set(TesseractOCRConfig::class.java, ocrConfig)
            bytes.inputStream().use { input ->
                autoParser.parse(input, handler, metadata, context)
            }
            handler.toString().trim()
        } catch (_: Exception) { "" }
        
        return ocrText
    }
}
