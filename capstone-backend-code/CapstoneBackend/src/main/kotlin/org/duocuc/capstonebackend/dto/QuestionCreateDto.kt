package org.duocuc.capstonebackend.dto

import java.util.UUID

data class QuestionCreateDto(
    val text: String,
    val correctAnswer: String,
    val subjectId: UUID,
    val topicId: UUID
)
