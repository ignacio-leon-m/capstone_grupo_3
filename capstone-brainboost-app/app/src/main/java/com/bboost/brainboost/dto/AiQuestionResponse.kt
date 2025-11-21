// NUEVO DTO
package com.bboost.brainboost.dto

data class AiQuestionsResponse(
    val saved: Int,
    val questions: List<QuestionResponseDto>
)

data class QuestionResponseDto(
    val text: String,
    val correctAnswer: String
)
