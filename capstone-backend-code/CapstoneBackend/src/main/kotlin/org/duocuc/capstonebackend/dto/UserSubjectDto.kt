package org.duocuc.capstonebackend.dto

import java.time.LocalDateTime
import java.util.UUID


// DTO to enroll a subject to a user (profesor or alumno).

data class EnrollSubjectDto(
    val userId: UUID,
    val subjectId: UUID
)


// DTO response with information about the enrollment user-subject.
data class UserSubjectDto(
    val id: UUID,
    val userId: UUID,
    val userName: String,
    val subjectId: UUID,
    val subjectName: String,
    val enrollmentDate: LocalDateTime,
    val active: Boolean,
    val userRole: String
)


// DTO with info about the subject
data class SubjectInfoDto(
    val id: UUID,
    val name: String,
    val enrollmentDate: LocalDateTime,
    val active: Boolean
)
