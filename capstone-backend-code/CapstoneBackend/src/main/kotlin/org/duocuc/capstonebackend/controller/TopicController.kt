package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.model.Topic
import org.duocuc.capstonebackend.repository.SubjectRepository
import org.duocuc.capstonebackend.repository.TopicRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

data class TopicDto(val id: UUID, val name: String)

@RestController
@RequestMapping("/api/subjects")
class TopicController(
    private val topicRepository: TopicRepository,
    private val subjectRepository: SubjectRepository
) {
    @GetMapping("/{subjectId}/topics")
    @PreAuthorize("hasAnyAuthority('alumno','profesor','admin')")
    fun getTopicsBySubject(@PathVariable subjectId: UUID): ResponseEntity<List<TopicDto>> {
        subjectRepository.findById(subjectId)
            .orElseThrow { IllegalArgumentException("Asignatura no encontrada") }

        val topics: List<Topic> = topicRepository.findBySubjectId(subjectId)
        return ResponseEntity.ok(topics.map { TopicDto(it.id!!, it.name) })
    }
}
