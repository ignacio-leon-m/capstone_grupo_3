package org.duocuc.capstonebackend.nosql

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.util.UUID

@Document(collection = "stored_documents")
data class StoredDocument(
    @Id
    val id: String = UUID.randomUUID().toString(),
    val userId: UUID,
    val fileName: String,
    val filePath: String,
    val mimeType: String,
    val sizeBytes: Long,
    val sha1: String,
    val createdAt: Instant = Instant.now()
)
