package org.duocuc.capstonebackend.nosql

import jakarta.persistence.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDateTime

@Document
data class DocumentAnalysis(
    @Id
    val id: String? = null, // Campo ID, mapea a _id de MongoDB [cite: 2623]
    @Field("original_filename")
    val originalFilename: String,
    @Field("file_content_base64")
    val fileContentBase64: String, // PDF en Base64
    val analysisResult: String,    // Contenido generado por Gemini
    val analysisDate: LocalDateTime = LocalDateTime.now(),
    @Field("user_id")
    val userId: String? = null
)
