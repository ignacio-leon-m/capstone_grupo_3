package org.duocuc.capstonebackend.service

import org.duocuc.capstonebackend.dto.ConceptCreateDto
import org.duocuc.capstonebackend.dto.ConceptResponseDto
import org.duocuc.capstonebackend.exception.ResourceNotFoundException
import org.duocuc.capstonebackend.model.Concept
import org.duocuc.capstonebackend.repository.ConceptRepository
import org.duocuc.capstonebackend.repository.TopicRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
class ConceptService(
    private val conceptRepository: ConceptRepository,
    private val topicRepository: TopicRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    @Transactional
    fun createConcepts(topicId: UUID, concepts: List<ConceptCreateDto>): List<ConceptResponseDto> {
        logger.info("Creating ${concepts.size} concepts for topic $topicId")

        val topic = topicRepository.findById(topicId)
            .orElseThrow { ResourceNotFoundException("Tema no encontrado con ID: $topicId") }

        val entities = concepts.map { dto ->
            Concept(
                word = dto.word,
                hint = dto.hint,
                topic = topic,
                createdAt = LocalDateTime.now()
            )
        }

        val saved = conceptRepository.saveAll(entities)
        logger.info("Successfully created ${saved.size} concepts")

        return saved.map { it.toResponseDto() }
    }

    fun getConceptsByTopic(topicId: UUID): List<ConceptResponseDto> {
        logger.debug("Fetching concepts for topic $topicId")

        if (!topicRepository.existsById(topicId)) {
            throw ResourceNotFoundException("Tema no encontrado con ID: $topicId")
        }

        return conceptRepository.findByTopicId(topicId).map { it.toResponseDto() }
    }

    fun searchConcepts(query: String): List<ConceptResponseDto> {
        logger.debug("Searching concepts with query: $query")
        return conceptRepository.findByWordContainingIgnoreCase(query).map { it.toResponseDto() }
    }

    fun getConceptById(conceptId: UUID): ConceptResponseDto {
        logger.debug("Fetching concept with ID: $conceptId")
        
        val concept = conceptRepository.findById(conceptId)
            .orElseThrow { ResourceNotFoundException("Concepto no encontrado con ID: $conceptId") }
        
        return concept.toResponseDto()
    }

    private fun Concept.toResponseDto() = ConceptResponseDto(
        id = id!!,
        word = word,
        hint = hint,
        topicId = topic.id!!,
        topicName = topic.name,
        createdAt = createdAt.format(dateFormatter)
    )
}
