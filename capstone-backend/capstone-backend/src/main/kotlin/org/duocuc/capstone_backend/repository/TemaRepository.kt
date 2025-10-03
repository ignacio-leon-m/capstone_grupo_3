package org.duocuc.capstone_backend.repository

import org.duocuc.capstone_backend.model.Tema
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TemaRepository : JpaRepository<Tema, UUID> {
    fun findByMateriaIdOrderByCreadoEnDesc(materiaId: UUID): List<Tema>
}
