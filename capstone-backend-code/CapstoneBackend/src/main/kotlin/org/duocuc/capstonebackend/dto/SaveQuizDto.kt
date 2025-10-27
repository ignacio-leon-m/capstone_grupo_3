package org.duocuc.capstonebackend.dto

import java.util.UUID

data class SaveQuizRequestDto(
    val subjectId: UUID,
    val topicName: String,
    val questions: List<AiQuizQuestionDto>
)


