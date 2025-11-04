package org.duocuc.capstonebackend.dto

import java.util.UUID

data class SubjectDto(
    val id: UUID,
    val nombre: String,
    val carreraNombre: String,
    val semestres: List<String> = emptyList()
)