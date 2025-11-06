package org.duocuc.capstonebackend.repository

import org.duocuc.capstonebackend.model.UsuarioAsignatura
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repositorio para gestionar la relación entre usuarios (profesores/alumnos) y asignaturas.
 */
@Repository
interface UsuarioAsignaturaRepository : JpaRepository<UsuarioAsignatura, UUID> {
    
    /**
     * Encuentra todas las asignaturas de un usuario.
     */
    fun findByUsuarioId(usuarioId: UUID): List<UsuarioAsignatura>
    
    /**
     * Encuentra todos los usuarios de una asignatura.
     */
    fun findByAsignaturaId(asignaturaId: UUID): List<UsuarioAsignatura>
    
    /**
     * Encuentra relaciones activas de un usuario.
     */
    fun findByUsuarioIdAndActivoTrue(usuarioId: UUID): List<UsuarioAsignatura>
    
    /**
     * Verifica si existe una relación entre usuario y asignatura.
     */
    fun existsByUsuarioIdAndAsignaturaId(usuarioId: UUID, asignaturaId: UUID): Boolean
    
    /**
     * Busca una relación específica entre usuario y asignatura.
     */
    fun findByUsuarioIdAndAsignaturaId(usuarioId: UUID, asignaturaId: UUID): UsuarioAsignatura?
    
    /**
     * Cuenta relaciones activas de un usuario.
     */
    fun countByUsuarioIdAndActivoTrue(usuarioId: UUID): Long
    
    /**
     * Encuentra profesores de una asignatura (filtrado por rol).
     */
    @Query(
        """
        SELECT ua FROM UsuarioAsignatura ua 
        WHERE ua.asignatura.id = :asignaturaId 
        AND LOWER(ua.usuario.role.name) = 'profesor'
        """
    )
    fun findProfesoresByAsignatura(asignaturaId: UUID): List<UsuarioAsignatura>
    
    /**
     * Encuentra alumnos de una asignatura (filtrado por rol).
     */
    @Query(
        """
        SELECT ua FROM UsuarioAsignatura ua 
        WHERE ua.asignatura.id = :asignaturaId 
        AND LOWER(ua.usuario.role.name) = 'alumno'
        """
    )
    fun findAlumnosByAsignatura(asignaturaId: UUID): List<UsuarioAsignatura>
}
