package org.duocuc.capstonebackend.dto

import java.time.LocalDateTime
import java.util.UUID

/**
 * Response after uploading and processing a document.
 * Contains the extracted content summary.
 */
data class DocumentUploadResponseDto(
    val documentId: UUID,
    val fileName: String,
    val subjectId: UUID,
    val uploadedAt: LocalDateTime,
    val processed: Boolean,
    val topicId: UUID?,
    val topicName: String?,
    val conceptsExtracted: Int,
    val message: String
)

/**
 * Detailed information about a processed document.
 */
data class DocumentDetailDto(
    val documentId: UUID,
    val fileName: String,
    val documentType: String,
    val sizeBytes: Long,
    val subjectId: UUID,
    val subjectName: String,
    val uploadedBy: UUID,
    val uploadedByName: String,
    val uploadedAt: LocalDateTime,
    val processed: Boolean,
    val processedAt: LocalDateTime?,
    val conceptsExtracted: Int?,
    val topics: List<TopicSummaryDto>
)

/**
 * Summary of a topic with concept count.
 */
data class TopicSummaryDto(
    val topicId: UUID,
    val topicName: String,
    val conceptCount: Int
)

/**
 * Concept extracted from document with hint.
 */
data class ConceptDto(
    val conceptId: UUID?,
    val word: String,
    val hint: String?
)

/**
 * Request to manually create concepts for a topic.
 */
data class CreateConceptsRequestDto(
    val topicId: UUID,
    val concepts: List<ConceptInputDto>
)

data class ConceptInputDto(
    val word: String,
    val hint: String?
)
