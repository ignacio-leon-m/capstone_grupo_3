package com.bboost.brainboost.dto

import java.util.UUID

data class SubjectDto(
    val id: UUID,
    val nombre: String,
    val carreraNombre: String,
    val semestres: List<String> = emptyList()
)