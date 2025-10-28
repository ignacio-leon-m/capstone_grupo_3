package org.duocuc.capstonebackend.nosql

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.util.UUID

@Document("ai_summaries")
data class AiSummaryDoc(
    @Id val id: String? = null,
    val userId: UUID,
    val title: String,
    val mimeType: String,
    val sizeBytes: Long,
    val sha1: String,
    val summary: String,
    val createdAt: Instant = Instant.now()
)
