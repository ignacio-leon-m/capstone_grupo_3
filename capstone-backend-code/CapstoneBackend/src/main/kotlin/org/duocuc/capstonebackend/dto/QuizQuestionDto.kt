package org.duocuc.capstonebackend.dto

import java.util.*

data class QuizQuestionDto(
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val subjectId: UUID,
    val topicId: UUID
)

data class QuizQuestionsPayload(
    val questions: List<QuizQuestionDto>
)
