package org.duocuc.capstonebackend.dto

import java.time.LocalDateTime
import java.util.UUID

data class AssignSubjectDto(
    val userId: UUID,
    val subjectId: UUID
)
data class UserSubjectDto(
    val id: UUID,
    val userId: UUID,
    val userName: String,
    val subjectId: UUID,
    val subjectName: String,
    val assignDate: LocalDateTime,
    val active: Boolean,
    val userRole: String
)

data class SimpleSubjectDto(
    val id: UUID,
    val name: String,
    val assignmentDate: LocalDateTime,
    val active: Boolean
)
