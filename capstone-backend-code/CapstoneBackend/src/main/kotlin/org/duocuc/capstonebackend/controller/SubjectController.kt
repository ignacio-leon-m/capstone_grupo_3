package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.model.Subject
import org.duocuc.capstonebackend.repository.SubjectRepository
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/subjects")
class SubjectController(
    private val subjectRepository: SubjectRepository
) {
    data class SubjectBasicDto(val id: java.util.UUID, val name: String)

    @GetMapping
    @PreAuthorize("hasAnyAuthority('admin','profesor')")
    fun list(): List<SubjectBasicDto> = subjectRepository.findAll()
        .mapNotNull { s -> s.id?.let { SubjectBasicDto(id = it, name = s.name) } }
}
