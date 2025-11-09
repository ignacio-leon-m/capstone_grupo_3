package org.duocuc.capstonebackend.dto

import java.util.UUID

data class ConceptCreateDto(
    val word: String,
    val hint: String?,
    val topicId: UUID
)

data class ConceptResponseDto(
    val id: UUID,
    val word: String,
    val hint: String?,
    val topicId: UUID,
    val topicName: String,
    val createdAt: String
)
