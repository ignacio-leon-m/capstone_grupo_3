package org.duocuc.capstonebackend.util

import org.apache.tika.Tika

object PdfTextExtractor {
    private val tika = Tika()

    fun safeExtract(bytes: ByteArray): String = try {
        tika.parseToString(bytes.inputStream()).trim()
    } catch (_: Exception) {
        "" // devolver vacío si no se puede extraer
    }
}
