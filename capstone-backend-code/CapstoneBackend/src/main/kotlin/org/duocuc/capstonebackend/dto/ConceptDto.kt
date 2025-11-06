package org.duocuc.capstonebackend.dto

import java.util.UUID

/**
 * DTO to create a new concept (usually from AI extraction).
 */
data class ConceptCreateDto(
    val word: String,
    val hint: String?,
    val topicId: UUID
)

/**
 * DTO for concept response.
 */
data class ConceptResponseDto(
    val id: UUID,
    val word: String,
    val hint: String?,
    val topicId: UUID,
    val topicName: String,
    val createdAt: String
)
