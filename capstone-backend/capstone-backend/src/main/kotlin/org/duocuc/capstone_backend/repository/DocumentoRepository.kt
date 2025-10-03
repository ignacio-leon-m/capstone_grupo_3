package org.duocuc.capstone_backend.repository

import org.duocuc.capstone_backend.model.Documento
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DocumentoRepository : JpaRepository<Documento, UUID> {
    fun findByTemaId(temaId: UUID): List<Documento>
    fun countByTemaId(temaId: UUID): Long
}
