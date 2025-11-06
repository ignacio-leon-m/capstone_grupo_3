package org.duocuc.capstonebackend.dto

import java.time.LocalDateTime
import java.util.UUID

/**
 * DTO para asignar una asignatura a un usuario (profesor o alumno).
 */
data class AsignarAsignaturaDto(
    val usuarioId: UUID,
    val asignaturaId: UUID
)

/**
 * DTO de respuesta con información de la asignación usuario-asignatura.
 */
data class UsuarioAsignaturaDto(
    val id: UUID,
    val usuarioId: UUID,
    val usuarioNombre: String,
    val asignaturaId: UUID,
    val asignaturaNombre: String,
    val fechaAsignacion: LocalDateTime,
    val activo: Boolean,
    val rolUsuario: String
)

/**
 * DTO simplificado con solo información de la asignatura.
 */
data class AsignaturaSimpleDto(
    val id: UUID,
    val nombre: String,
    val fechaAsignacion: LocalDateTime,
    val activo: Boolean
)
