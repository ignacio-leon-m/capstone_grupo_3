package com.bboost.brainboost.dto

data class AiQuizQuestionDto(
    val question: String,
    val options: List<String>,
    val answerIndex: Int
)