package org.duocuc.capstone_backend.dto

import java.time.LocalDateTime
import java.util.UUID

data class TemaListItemDto(
    val id: UUID,
    val titulo: String,
    val resumenPreview: String?,
    val documentos: Int,
    val creadoEn: LocalDateTime
)

data class TemaDetailDto(
    val id: UUID,
    val materiaId: UUID,
    val titulo: String,
    val descripcion: String?,
    val resumen: String?,
    val documentos: List<DocumentoDto>
)
