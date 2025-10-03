package org.duocuc.capstone_backend.dto

import com.fasterxml.jackson.databind.JsonNode
import java.util.UUID
import org.duocuc.capstone_backend.model.Documento
import org.duocuc.capstone_backend.model.Tema

data class DocumentoDto(
    val id: UUID,
    val fileName: String,
    val mimeType: String,
    val sizeBytes: Long
)

data class TemaResponseDto(
    val id: UUID,
    val materiaId: UUID,
    val titulo: String,
    val descripcion: String?,
    val resumen: String?,
    val quiz: JsonNode?,
    val documentos: List<DocumentoDto>
) {
    companion object {
        fun from(tema: Tema, documentos: List<Documento>): TemaResponseDto {
            val documentoDtos = documentos.map { doc ->
                DocumentoDto(
                    id = doc.id!!,
                    fileName = doc.fileName,
                    mimeType = doc.mimeType,
                    sizeBytes = doc.sizeBytes
                )
            }
            return TemaResponseDto(
                id = tema.id!!,
                materiaId = tema.materia.id!!,
                titulo = tema.titulo,
                descripcion = tema.descripcion,
                resumen = tema.resumen,
                quiz = tema.quiz,
                documentos = documentoDtos
            )
        }
    }
}
