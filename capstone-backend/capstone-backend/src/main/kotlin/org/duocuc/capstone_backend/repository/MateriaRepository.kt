package org.duocuc.capstone_backend.repository

import org.duocuc.capstone_backend.model.Materia
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface MateriaRepository : JpaRepository<Materia, UUID> {

    // Trae materias con su carrera en una sola query (evita N+1)
    @Query("select m from Materia m join fetch m.carrera")
    fun findAllWithCarrera(): List<Materia>
}
