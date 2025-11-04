package org.duocuc.capstonebackend.repository

import org.duocuc.capstonebackend.model.Subject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SubjectRepository: JpaRepository<Subject, UUID> {

    // Asignaturas de un profesor específico (a través de semestres -> asignaturas_semestre)
    @Query("""
        SELECT DISTINCT s FROM Subject s 
        JOIN s.semesters sem 
        JOIN sem.professors prof 
        WHERE prof.id = :professorId
    """)
    fun findByProfessorId(@Param("professorId") professorId: UUID): List<Subject>

    // Todas las asignaturas (para admin)
    @Query("SELECT s FROM Subject s")
    fun findAllSubjects(): List<Subject>

    // Asignaturas por carrera (NUEVO MÉTODO - para la versión alternativa)
    fun findByDegreeId(@Param("degreeId") degreeId: UUID): List<Subject>
}