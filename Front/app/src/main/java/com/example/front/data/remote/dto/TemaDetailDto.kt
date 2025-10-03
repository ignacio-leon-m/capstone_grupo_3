package com.example.front.data.remote.dto

data class TemaDetailDto(
    val id: String,
    val materiaId: String,
    val titulo: String,
    val descripcion: String?,
    val resumen: String?,
    val documentos: List<DocumentoDto>
)
