package org.duocuc.capstonebackend.dto

data class AiQuizQuestionDto(
    val question: String,
    val options: List<String>,
    val answerIndex: Int
)

data class AiQuizDto(
    val questions: List<AiQuizQuestionDto>
)
