package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.dto.EnrollSubjectDto
import org.duocuc.capstonebackend.model.Subject
import org.duocuc.capstonebackend.repository.SubjectRepository
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
    private val subjectRepository: SubjectRepository
) {
    data class SubjectBasicDto(val id: java.util.UUID, val name: String)

    @GetMapping("{professorId}/subjects")
    fun getEnrolledSubjects(professorId: UUID): List<EnrollSubjectDto> {
        val enrolledSubjects = subjectRepository.findEnrolledSubjects(professorId);
        return enrolledSubjects
    }
}
