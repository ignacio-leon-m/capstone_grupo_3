package org.duocuc.capstonebackend.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.duocuc.capstonebackend.dto.ConceptCreateDto
import org.duocuc.capstonebackend.service.AiService
import org.duocuc.capstonebackend.service.ConceptService
import org.duocuc.capstonebackend.service.FileUploadService
import org.duocuc.capstonebackend.util.PdfTextExtractor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("/api/ai")
class AiController(
    private val conceptService: ConceptService,
    private val fileUploadService: FileUploadService,
    @param:Qualifier("persistingAiService") private val aiService: AiService
) {
    private val log = LoggerFactory.getLogger(AiController::class.java)
    private val objectMapper = jacksonObjectMapper()

    data class ConceptGenerationResponse(
        val inserted: Int,
        val examples: List<Map<String, String>>
    )

    @PostMapping(
        "/pdf/concepts",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("hasAnyAuthority('profesor','admin')")
    fun generateConceptsFromPdf(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("topicId") topicId: UUID,
        @RequestParam("max", defaultValue = "30") max: Int
    ): ResponseEntity<ConceptGenerationResponse> {
        return try {
            log.info("Generating $max concepts from PDF for topic $topicId")

            // 1. Save PDF document
            val storedDocument = fileUploadService.savePdf(file)
            log.info("PDF saved with ID: ${storedDocument.id}")

            // 2. Extract text from PDF
            val text = PdfTextExtractor.safeExtract(file.bytes)
            if (text.isBlank()) {
                log.warn("PDF contains no extractable text")
                return ResponseEntity.badRequest().build()
            }
            log.info("Extracted ${text.length} characters from PDF")

            // 3. Create prompt for AI
            val prompt = """
                A partir del siguiente texto educativo, genera exactamente $max conceptos clave en formato JSON.
                
                Cada concepto debe tener:
                - "word": la palabra o término clave (máximo 50 caracteres)
                - "hint": una pista descriptiva que ayude a entender el concepto (máximo 500 caracteres)
                
                Requisitos:
                - Los conceptos deben ser relevantes y educativos
                - Las palabras deben ser términos técnicos o conceptos importantes del texto
                - Las pistas deben ser claras y ayudar a comprender el concepto
                - Responde ÚNICAMENTE con un array JSON válido, sin texto adicional
                
                Formato de respuesta:
                [
                  {"word": "concepto1", "hint": "descripción del concepto1"},
                  {"word": "concepto2", "hint": "descripción del concepto2"}
                ]
                
                Texto:
            """.trimIndent()

            // 4. Query AI service
            val aiResponse = aiService.query(text, prompt)
            log.debug("AI Response: $aiResponse")

            // 5. Parse JSON response
            val concepts: List<ConceptCreateDto> = try {
                // Try to parse the response, cleaning any markdown code blocks
                val cleanedResponse = aiResponse
                    .replace("```json", "")
                    .replace("```", "")
                    .trim()
                
                objectMapper.readValue<List<Map<String, String?>>>(cleanedResponse)
                    .map { map ->
                        ConceptCreateDto(
                            word = map["word"] ?: throw IllegalArgumentException("Missing 'word' field"),
                            hint = map["hint"],
                            topicId = topicId
                        )
                    }
                    .take(max) // Ensure we don't exceed max
            } catch (e: Exception) {
                log.error("Failed to parse AI response as JSON: ${e.message}", e)
                log.error("AI Response was: $aiResponse")
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
            }

            if (concepts.isEmpty()) {
                log.warn("No concepts were generated")
                return ResponseEntity.ok(ConceptGenerationResponse(0, emptyList()))
            }

            // 6. Save concepts to database
            val savedConcepts = conceptService.createConcepts(topicId, concepts)
            log.info("Successfully saved ${savedConcepts.size} concepts")

            // 7. Create response with examples (first 3)
            val examples: List<Map<String, String>> = savedConcepts.take(3).map { concept ->
                mapOf(
                    "word" to concept.word,
                    "hint" to (concept.hint ?: "")
                )
            }

            ResponseEntity.ok(
                ConceptGenerationResponse(
                    inserted = savedConcepts.size,
                    examples = examples
                )
            )
        } catch (e: Exception) {
            log.error("Error generating concepts from PDF", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}
