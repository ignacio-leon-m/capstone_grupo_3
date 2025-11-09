package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.dto.*
import org.duocuc.capstonebackend.service.ContentService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

/**
 * Content Service Controller - Central API for academic content management.
 * 
 * Architecture alignment:
 * - Professors upload documents (PDF, DOCX, TXT)
 * - Content Service processes and extracts concepts via IA Service
 * - Content is stored in PostgreSQL for ALL games (Hangman, Criss-Cross, Quiz)
 * - Game Engines fetch content via topicId (decoupled from document upload)
 * 
 * This controller implements the "Content Service" microservice from project architecture.
 */
@RestController
@RequestMapping("/api/content")
@CrossOrigin(origins = ["*"])
class ContentController(
    private val contentService: ContentService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Upload and process a document to extract concepts.
     * 
     * Workflow:
     * 1. Professor uploads PDF/DOCX
     * 2. Content Service saves document
     * 3. Extracts text from file
     * 4. Queries IA Service to extract key concepts
     * 5. Creates Topic + Concepts in database
     * 6. Returns summary with topicId
     * 
     * The returned topicId can then be used by ANY game engine:
     * - POST /api/hangman/start (with topicId)
     * - POST /api/criss-cross/start (with topicId)
     * - POST /api/quiz/start (with topicId)
     * 
     * @param userId Professor uploading content
     * @param subjectId Subject this content belongs to
     * @param file Document file (PDF, DOCX, TXT)
     * @return Document upload summary with topicId
     */


    @PostMapping("/upload-document")
    @PreAuthorize("hasAnyAuthority('profesor', 'admin')")
    fun uploadDocument(
        @RequestParam userId: UUID,
        @RequestParam subjectId: UUID,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<DocumentUploadResponseDto> {
        logger.info("Content upload request from user $userId for subject $subjectId, file: ${file.originalFilename}")
        val response = contentService.uploadAndProcessDocument(userId, subjectId, file)
        logger.info("Document processed successfully. Topic ID: ${response.topicId}, Concepts: ${response.conceptsExtracted}")
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    /**
     * Get all concepts for a specific topic.
     * Used by Game Engines to fetch content for gameplay.
     * 
     * @param topicId Topic identifier
     * @return List of concepts with hints
     */
    @GetMapping("/topics/{topicId}/concepts")
    @PreAuthorize("hasAnyAuthority('profesor', 'admin', 'alumno')")
    fun getConceptsByTopic(@PathVariable topicId: UUID): ResponseEntity<List<ConceptDto>> {
        logger.debug("Fetching concepts for topic $topicId")
        val concepts = contentService.getConceptsByTopic(topicId)
        return ResponseEntity.ok(concepts)
    }

    /**
     * Get detailed information about a document.
     * 
     * @param documentId Document identifier
     * @return Document details with topics and concepts
     */
    @GetMapping("/documents/{documentId}")
    @PreAuthorize("hasAnyAuthority('profesor', 'admin')")
    fun getDocumentDetails(@PathVariable documentId: UUID): ResponseEntity<DocumentDetailDto> {
        logger.debug("Fetching details for document $documentId")
        val details = contentService.getDocumentDetails(documentId)
        return ResponseEntity.ok(details)
    }

    /**
     * List all documents for a subject.
     * 
     * @param subjectId Subject identifier
     * @return List of documents with summaries
     */
    @GetMapping("/subjects/{subjectId}/documents")
    @PreAuthorize("hasAnyAuthority('profesor', 'admin')")
    fun getDocumentsBySubject(@PathVariable subjectId: UUID): ResponseEntity<List<DocumentDetailDto>> {
        logger.debug("Fetching documents for subject $subjectId")
        val documents = contentService.getDocumentsBySubject(subjectId)
        return ResponseEntity.ok(documents)
    }

    /**
     * Manually create concepts for a topic.
     * Allows professors to add custom concepts without uploading documents.
     * 
     * @param request Concepts to create
     * @return Created concepts
     */
    @PostMapping("/concepts")
    @PreAuthorize("hasAnyAuthority('profesor', 'admin')")
    fun createConcepts(@RequestBody request: CreateConceptsRequestDto): ResponseEntity<List<ConceptDto>> {
        logger.info("Manual concept creation for topic ${request.topicId}, count: ${request.concepts.size}")
        val concepts = contentService.createConcepts(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(concepts)
    }
}
