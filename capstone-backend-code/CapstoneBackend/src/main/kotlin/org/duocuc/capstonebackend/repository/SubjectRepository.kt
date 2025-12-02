package org.duocuc.capstonebackend.repository

import org.duocuc.capstonebackend.dto.EnrollSubjectDto
import org.duocuc.capstonebackend.model.Subject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SubjectRepository : JpaRepository<Subject, UUID> {
    fun findEnrolledSubjects(professorId: UUID): List<EnrollSubjectDto>
}