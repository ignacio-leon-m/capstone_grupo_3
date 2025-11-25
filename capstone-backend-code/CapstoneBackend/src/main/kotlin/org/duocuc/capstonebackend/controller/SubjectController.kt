package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.model.Subject
import org.duocuc.capstonebackend.repository.SubjectRepository
import org.duocuc.capstonebackend.repository.TopicRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/subjects")
class SubjectController(
    private val subjectRepository: SubjectRepository,
    private val topicRepository: TopicRepository
) {
    data class SubjectBasicDto(val id: java.util.UUID, val name: String)
    data class TopicDto(val id: java.util.UUID, val name: String)

    @GetMapping
    @PreAuthorize("hasAnyAuthority('admin','profesor')")
    fun list(): List<SubjectBasicDto> = subjectRepository.findAll()
        .mapNotNull { s -> s.id?.let { SubjectBasicDto(id = it, name = s.name) } }

    @GetMapping("/{subjectId}/topics")
    @PreAuthorize("hasAnyAuthority('admin','profesor','alumno')")
    fun getTopicsBySubject(@PathVariable subjectId: UUID): ResponseEntity<List<TopicDto>> {
        val topics = topicRepository.findBySubjectId(subjectId)
        val topicsDto = topics.mapNotNull { topic ->
            topic.id?.let { TopicDto(id = it, name = topic.name) }
        }
        return ResponseEntity.ok(topicsDto)
    }
}
