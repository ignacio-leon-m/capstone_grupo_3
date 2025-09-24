package org.duocuc.capstone_backend.ai

import org.apache.tika.Tika
import org.springframework.stereotype.Service
import org.duocuc.capstone_backend.storage.StorageStreamOpener

@Service
class TextExtractor(
    private val opener: StorageStreamOpener
) {
    private val tika = Tika()

    /** Extrae texto plano del archivo apuntado por storagePath. */
    fun extract(storagePath: String, maxChars: Int = 20_000): String {
        opener.open(storagePath).use { input ->
            val all = tika.parseToString(input) // cierra el stream al terminar
            return all.replace(Regex("\\s+"), " ").trim().take(maxChars)
        }
    }
}
