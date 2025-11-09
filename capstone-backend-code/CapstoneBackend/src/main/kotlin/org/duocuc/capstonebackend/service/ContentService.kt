package org.duocuc.capstonebackend.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.duocuc.capstonebackend.dto.*
import org.duocuc.capstonebackend.exception.BadRequestException
import org.duocuc.capstonebackend.exception.ResourceNotFoundException
import org.duocuc.capstonebackend.model.Concept
import org.duocuc.capstonebackend.model.Document
import org.duocuc.capstonebackend.model.Topic
import org.duocuc.capstonebackend.repository.*
import org.duocuc.capstonebackend.util.PdfTextExtractor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.util.UUID

/**
 * Content Service - Central service for academic content management.
 * 
 * Responsibilities (as per project architecture):
 * - Upload and store documents (PDF, DOCX, TXT)
 * - Extract concepts/questions via IA Service
 * - Persist content in PostgreSQL (documents, topics, concepts)
 * - Provide content for ALL game engines (Hangman, Criss-Cross, Quiz)
 * 
 * Architecture alignment:
 * - Connects to IA Service for AI-powered extraction
 * - Stores in PostgreSQL for transactional integrity
 * - Decoupled from game logic (Game Engine responsibility)
 */
@Service
class ContentService(
    @param:Qualifier("persistingAiService") private val aiService: AiService,
    private val fileUploadService: FileUploadService,
    private val documentRepository: DocumentRepository,
    private val subjectRepository: SubjectRepository,
    private val topicRepository: TopicRepository,
    private val conceptRepository: ConceptRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val objectMapper = jacksonObjectMapper()

    companion object {
        const val MIN_CONCEPTS_FOR_GAME = 10
        const val MAX_CONCEPTS_TO_EXTRACT = 20
    }

    /**
     * Upload a document and extract concepts automatically via AI.
     * 
     * Flow:
     * 1. Validate user (professor) and subject
     * 2. Save PDF file to disk
     * 3. Extract text from PDF
     * 4. Query IA Service to extract key concepts
     * 5. Create Document record
     * 6. Create Topic for this content
     * 7. Store extracted Concepts
     * 
     * This content is then REUSABLE for any game (Hangman, Criss-Cross, Quiz).
     * 
     * @param userId Professor uploading the content
     * @param subjectId Subject this content belongs to
     * @param file PDF/DOCX/TXT file
     * @return Document upload summary with topic and concepts
     */
    @Transactional
    fun uploadAndProcessDocument(
        userId: UUID,
        subjectId: UUID,
        file: MultipartFile
    ): DocumentUploadResponseDto {
        logger.info("Content Service: Processing document upload from user $userId, subject $subjectId, file: ${file.originalFilename}")

        // Validate user exists
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User not found with ID: $userId") }

        // Validate subject exists
        val subject = subjectRepository.findById(subjectId)
            .orElseThrow { ResourceNotFoundException("Subject not found with ID: $subjectId") }

        // Save file to disk (Mongo)
        val storedDocument = fileUploadService.savePdf(file)
        logger.debug("File saved with ID: ${storedDocument.id}, path: ${storedDocument.fileName}")

        // Extract text from file
        val extractedText = when {
            file.originalFilename?.endsWith(".pdf", ignoreCase = true) == true -> {
                PdfTextExtractor.safeExtract(file.bytes)
            }
            else -> {
                // For TXT or other text files, read directly
                String(file.bytes, Charsets.UTF_8)
            }
        }

        if (extractedText.isBlank()) {
            throw BadRequestException("File is empty or text could not be extracted")
        }
        logger.debug("Extracted ${extractedText.length} characters from file")

        // Create Document record
        val document = Document(
            fileName = file.originalFilename ?: "unknown",
            filePath = storedDocument.fileName, // Using stored filename as path
            documentType = file.contentType ?: "application/pdf",
            sizeBytes = file.size,
            subject = subject,
            uploadedBy = user,
            uploadedAt = LocalDateTime.now(),
            processed = false,
            extractedText = extractedText
        )
        val savedDocument = documentRepository.save(document)
        logger.info("Document record created with ID: ${savedDocument.id}")

        // Extract concepts via AI
        val prompt = buildConceptExtractionPrompt()
        val aiResponse = aiService.query(extractedText, prompt)
        logger.debug("AI response received for concept extraction")

        val extractedConcepts = parseAiConceptsResponse(aiResponse)
        if (extractedConcepts.size < MIN_CONCEPTS_FOR_GAME) {
            throw BadRequestException(
                "Not enough concepts extracted. Found ${extractedConcepts.size}, need at least $MIN_CONCEPTS_FOR_GAME"
            )
        }
        logger.info("Successfully extracted ${extractedConcepts.size} concepts")

        // Create Topic for this document
        val topicName = generateTopicName(file.originalFilename ?: "Document")
        val topic = Topic(
            name = topicName,
            subject = subject,
            document = savedDocument
        )
        val savedTopic = topicRepository.save(topic)
        logger.info("Topic created: ${savedTopic.name} with ID: ${savedTopic.id}")

        // Save extracted concepts
        val conceptEntities = extractedConcepts.map { conceptData ->
            Concept(
                word = conceptData.word,
                hint = conceptData.hint,
                topic = savedTopic
            )
        }
        val savedConcepts = conceptRepository.saveAll(conceptEntities)
        logger.info("Saved ${savedConcepts.count()} concepts to database")

        // Update document as processed
        savedDocument.processed = true
        savedDocument.processedAt = LocalDateTime.now()
        savedDocument.conceptsExtracted = savedConcepts.count()
        documentRepository.save(savedDocument)

        return DocumentUploadResponseDto(
            documentId = savedDocument.id!!,
            fileName = savedDocument.fileName,
            subjectId = subject.id!!,
            uploadedAt = savedDocument.uploadedAt,
            processed = true,
            topicId = savedTopic.id,
            topicName = savedTopic.name,
            conceptsExtracted = savedConcepts.count(),
            message = "Document processed successfully. Ready for games."
        )
    }

    /**
     * Get all concepts for a specific topic.
     * Used by Game Engines to fetch content for gameplay.
     */
    fun getConceptsByTopic(topicId: UUID): List<ConceptDto> {
        val topic = topicRepository.findById(topicId)
            .orElseThrow { ResourceNotFoundException("Topic not found with ID: $topicId") }

        val concepts = conceptRepository.findByTopic(topic)
        return concepts.map { concept ->
            ConceptDto(
                conceptId = concept.id,
                word = concept.word,
                hint = concept.hint
            )
        }
    }

    /**
     * Get document details with all topics and concepts.
     */
    fun getDocumentDetails(documentId: UUID): DocumentDetailDto {
        val document = documentRepository.findById(documentId)
            .orElseThrow { ResourceNotFoundException("Document not found with ID: $documentId") }

        // Find topics associated with this document
        val topics = topicRepository.findByDocument(document).map { topic ->
            val conceptCount = conceptRepository.countByTopic(topic)
            TopicSummaryDto(
                topicId = topic.id!!,
                topicName = topic.name,
                conceptCount = conceptCount
            )
        }

        return DocumentDetailDto(
            documentId = document.id!!,
            fileName = document.fileName,
            documentType = document.documentType,
            sizeBytes = document.sizeBytes,
            subjectId = document.subject.id!!,
            subjectName = document.subject.name,
            uploadedBy = document.uploadedBy.id!!,
            uploadedByName = "${document.uploadedBy.firstName} ${document.uploadedBy.lastName}",
            uploadedAt = document.uploadedAt,
            processed = document.processed,
            processedAt = document.processedAt,
            conceptsExtracted = document.conceptsExtracted,
            topics = topics
        )
    }

    /**
     * List all documents for a subject.
     */
    fun getDocumentsBySubject(subjectId: UUID): List<DocumentDetailDto> {
        val subject = subjectRepository.findById(subjectId)
            .orElseThrow { ResourceNotFoundException("Subject not found with ID: $subjectId") }

        val documents = documentRepository.findBySubject(subject)
        return documents.map { doc -> getDocumentDetails(doc.id!!) }
    }

    /**
     * Manually create concepts for a topic (professor CRUD).
     */
    @Transactional
    fun createConcepts(request: CreateConceptsRequestDto): List<ConceptDto> {
        val topic = topicRepository.findById(request.topicId)
            .orElseThrow { ResourceNotFoundException("Topic not found with ID: ${request.topicId}") }

        val conceptEntities = request.concepts.map { input ->
            Concept(
                word = input.word,
                hint = input.hint,
                topic = topic
            )
        }

        val savedConcepts = conceptRepository.saveAll(conceptEntities)
        logger.info("Manually created ${savedConcepts.count()} concepts for topic ${topic.name}")

        return savedConcepts.map { concept ->
            ConceptDto(
                conceptId = concept.id,
                word = concept.word,
                hint = concept.hint
            )
        }
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Build AI prompt to extract concepts from text.
     */
    private fun buildConceptExtractionPrompt(): String {
        return """
            You are an educational content analyzer. Extract key concepts from the provided text.
            
            Requirements:
            - Extract between 10 and 20 key concepts
            - Each concept must be a SINGLE WORD (no spaces, no phrases)
            - Provide a short hint (max 10 words) for each concept
            - Focus on technical terms, important vocabulary, or core ideas
            
            Return ONLY a valid JSON array in this exact format:
            [
              {"word": "photosynthesis", "hint": "Process plants use to make food"},
              {"word": "mitochondria", "hint": "Powerhouse of the cell"},
              {"word": "enzyme", "hint": "Biological catalyst"}
            ]
            
            Do not include any explanation or text outside the JSON array.
        """.trimIndent()
    }

    /**
     * Parse AI response to extract concepts.
     */
    private fun parseAiConceptsResponse(aiResponse: String): List<ConceptData> {
        val concepts = mutableListOf<ConceptData>()

        try {
            // Try to find JSON array in response
            val jsonStart = aiResponse.indexOf('[')
            val jsonEnd = aiResponse.lastIndexOf(']')

            if (jsonStart == -1 || jsonEnd == -1) {
                logger.warn("No JSON array found in AI response")
                return emptyList()
            }

            val jsonText = aiResponse.substring(jsonStart, jsonEnd + 1)
            val jsonArray = objectMapper.readTree(jsonText)

            if (jsonArray.isArray) {
                for (node in jsonArray) {
                    parseConceptNode(node)?.let { concepts.add(it) }
                }
            }
        } catch (e: Exception) {
            logger.error("Error parsing AI response: ${e.message}", e)
            throw BadRequestException("Failed to parse AI response: ${e.message}")
        }

        // Filter and limit concepts
        return concepts
            .filter { it.word.length in 3..20 && !it.word.contains(' ') }
            .take(MAX_CONCEPTS_TO_EXTRACT)
    }

    /**
     * Parse a single concept node from JSON.
     */
    private fun parseConceptNode(node: JsonNode): ConceptData? {
        return try {
            val word = node.get("word")?.asText()
            val hint = node.get("hint")?.asText()

            if (word != null && word.isNotBlank()) {
                ConceptData(word = word.trim(), hint = hint?.trim())
            } else {
                null
            }
        } catch (e: Exception) {
            logger.warn("Failed to parse concept node: ${e.message}")
            null
        }
    }

    /**
     * Generate a topic name from filename.
     */
    private fun generateTopicName(fileName: String): String {
        val baseName = fileName.substringBeforeLast(".")
        val sanitized = baseName
            .replace(Regex("[^a-zA-Z0-9\\s-]"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
        return sanitized.take(100)
    }

    /**
     * Internal data class for concept extraction.
     */
    private data class ConceptData(
        val word: String,
        val hint: String?
    )
}
