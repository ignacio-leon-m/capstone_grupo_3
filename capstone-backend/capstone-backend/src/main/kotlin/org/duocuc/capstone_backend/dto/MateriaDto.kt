package org.duocuc.capstone_backend.dto

import java.util.UUID

data class MateriaDto(
    val id: UUID,
    val name: String,
    val carrera: String
)
