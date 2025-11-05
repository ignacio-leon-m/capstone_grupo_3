package org.duocuc.capstonebackend.dto

import java.util.UUID

/**
 * DTO para crear un nuevo concepto (generalmente desde extracción de IA).
 */
data class ConceptCreateDto(
    val palabraConcepto: String,
    val hint: String?,
    val idTema: UUID
)

/**
 * DTO para respuesta de concepto.
 */
data class ConceptResponseDto(
    val id: UUID,
    val palabraConcepto: String,
    val hint: String?,
    val idTema: UUID,
    val nombreTema: String,
    val fechaCreacion: String
)
