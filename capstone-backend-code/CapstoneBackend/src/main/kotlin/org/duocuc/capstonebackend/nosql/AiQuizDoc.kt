package org.duocuc.capstonebackend.nosql

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.util.UUID

@Document("ai_quizzes")
data class AiQuizDoc(
    @Id val id: String? = null,
    val userId: UUID,
    val sourceTitle: String?,
    val sourceSha1: String?,
    val numQuestions: Int,
    val payload: String,          // JSON del quiz
    val createdAt: Instant = Instant.now()
)
