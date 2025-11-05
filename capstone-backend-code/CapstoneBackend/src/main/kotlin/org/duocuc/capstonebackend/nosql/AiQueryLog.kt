package org.duocuc.capstonebackend.nosql

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.util.UUID

@Document(collection = "ai_query_logs")
data class AiQueryLog(
    @Id
    val id: String = UUID.randomUUID().toString(),
    val userId: UUID,
    val textSha1: String,
    val prompt: String,
    val response: String,
    val createdAt: Instant = Instant.now()
)
