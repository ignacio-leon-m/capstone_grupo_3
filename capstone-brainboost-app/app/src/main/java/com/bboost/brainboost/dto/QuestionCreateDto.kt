package com.bboost.brainboost.dto

import java.util.UUID

data class QuestionCreateDto(
    val text: String,
    val correctAnswer: String,
    val subjectId: UUID,
    val topicId: UUID
)
