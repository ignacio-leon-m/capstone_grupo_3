package org.duocuc.capstonebackend.dto

data class AiQuizQuestionDto(
    val question: String,
    val options: List<String>,
    val answerIndex: Int
)
