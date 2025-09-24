package org.duocuc.capstone_backend.service

import org.duocuc.capstone_backend.dto.DocumentoDto
import org.duocuc.capstone_backend.dto.TemaDetailDto
import org.duocuc.capstone_backend.dto.TemaListItemDto
import org.duocuc.capstone_backend.repository.DocumentoRepository
import org.duocuc.capstone_backend.repository.TemaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class TemaAlumnoService(
    private val temaRepo: TemaRepository,
    private val docRepo: DocumentoRepository
) {

    @Transactional(readOnly = true)
    fun listarPorMateria(materiaId: UUID): List<TemaListItemDto> {
        val temas = temaRepo.findByMateriaIdOrderByCreadoEnDesc(materiaId)
        return temas.map { t ->
            val countDocs = docRepo.countByTemaId(t.id!!)
            val preview = t.resumen?.let { it.take(200) + if (it.length > 200) "…" else "" }
            TemaListItemDto(
                id = t.id!!,
                titulo = t.titulo,
                resumenPreview = preview,
                documentos = countDocs.toInt(),
                creadoEn = t.creadoEn
            )
        }
    }

    @Transactional(readOnly = true)
    fun detalle(temaId: UUID): TemaDetailDto {
        val t = temaRepo.findById(temaId).orElseThrow { IllegalArgumentException("Tema no encontrado") }
        val documentos = docRepo.findByTemaId(temaId).map { d ->
            DocumentoDto(
                id = d.id!!,
                fileName = d.fileName,
                mimeType = d.mimeType,
                sizeBytes = d.sizeBytes
            )
        }
        return TemaDetailDto(
            id = t.id!!,
            materiaId = t.materia.id!!,
            titulo = t.titulo,
            descripcion = t.descripcion,
            resumen = t.resumen,
            documentos = documentos
        )
    }
}
