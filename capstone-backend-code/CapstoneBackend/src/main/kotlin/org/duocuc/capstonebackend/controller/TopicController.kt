package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.dto.TopicDto
import org.duocuc.capstonebackend.model.Topic
import org.duocuc.capstonebackend.repository.SubjectRepository
import org.duocuc.capstonebackend.repository.TopicRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/topics")
class TopicController(
    private val topicRepository: TopicRepository,
    private val subjectRepository: SubjectRepository
) {
    @GetMapping("/{subjectId}")
    fun getTopicsBySubject(subjectId: UUID): List<TopicDto> {
        val subject = subjectRepository.findById(subjectId)
            .orElseThrow { IllegalArgumentException("Asignatura no encontrada") }
        val topics: List<TopicDto> = topicRepository.findTopicsBySubjectId(subjectId)
        return topics
    }
}
