package org.duocuc.capstonebackend.service

import org.duocuc.capstonebackend.dto.AsignarAsignaturaDto
import org.duocuc.capstonebackend.dto.AsignaturaSimpleDto
import org.duocuc.capstonebackend.dto.UsuarioAsignaturaDto
import org.duocuc.capstonebackend.model.UsuarioAsignatura
import org.duocuc.capstonebackend.model.User
import org.duocuc.capstonebackend.repository.SubjectRepository
import org.duocuc.capstonebackend.repository.UserRepository
import org.duocuc.capstonebackend.repository.UsuarioAsignaturaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UsuarioAsignaturaService(
    private val usuarioAsignaturaRepository: UsuarioAsignaturaRepository,
    private val userRepository: UserRepository,
    private val subjectRepository: SubjectRepository
) {

    @Transactional
    fun asignarAsignatura(dto: AsignarAsignaturaDto): UsuarioAsignaturaDto {
        if (usuarioAsignaturaRepository.existsByUsuarioIdAndAsignaturaId(dto.usuarioId, dto.asignaturaId)) {
            throw IllegalArgumentException("El usuario ya está asignado a esta asignatura")
        }

        val usuario = userRepository.findById(dto.usuarioId)
            .orElseThrow { IllegalArgumentException("Usuario no encontrado con id: ${dto.usuarioId}") }
        
        val asignatura = subjectRepository.findById(dto.asignaturaId)
            .orElseThrow { IllegalArgumentException("Asignatura no encontrada con id: ${dto.asignaturaId}") }

        val nuevaAsignacion = UsuarioAsignatura(
            usuario = usuario,
            asignatura = asignatura
        )

        val asignacionGuardada = usuarioAsignaturaRepository.save(nuevaAsignacion)
        return toDto(asignacionGuardada)
    }

    fun obtenerAsignaturasProfesor(profesorId: UUID): List<AsignaturaSimpleDto> {
        val usuario = userRepository.findById(profesorId)
            .orElseThrow { IllegalArgumentException("Usuario no encontrado con id: $profesorId") }
        
        // val rolNombre = usuario.role.name
        // if (rolNombre.lowercase() != "profesor") {
        //     throw IllegalArgumentException("El usuario no es un profesor")
        // }

        val asignaciones = usuarioAsignaturaRepository.findByUsuarioId(profesorId)
        return asignaciones.map { asignacion ->
            AsignaturaSimpleDto(
                id = asignacion.asignatura.id!!,
                nombre = asignacion.asignatura.name,
                fechaAsignacion = asignacion.fechaAsignacion,
                activo = asignacion.activo
            )
        }
    }

    fun obtenerAsignaturasAlumno(alumnoId: UUID): List<AsignaturaSimpleDto> {
        val usuario = userRepository.findById(alumnoId)
            .orElseThrow { IllegalArgumentException("Usuario no encontrado con id: $alumnoId") }
        
        // val rolNombre = usuario.role.name
        // if (rolNombre.lowercase() != "alumno") {
        //     throw IllegalArgumentException("El usuario no es un alumno")
        // }

        val asignaciones = usuarioAsignaturaRepository.findByUsuarioId(alumnoId)
        return asignaciones.map { asignacion ->
            AsignaturaSimpleDto(
                id = asignacion.asignatura.id!!,
                nombre = asignacion.asignatura.name,
                fechaAsignacion = asignacion.fechaAsignacion,
                activo = asignacion.activo
            )
        }
    }

    fun obtenerProfesoresDeAsignatura(asignaturaId: UUID): List<UsuarioAsignaturaDto> {
        val asignaciones = usuarioAsignaturaRepository.findProfesoresByAsignatura(asignaturaId)
        return asignaciones.map { toDto(it) }
    }

    fun obtenerAlumnosDeAsignatura(asignaturaId: UUID): List<UsuarioAsignaturaDto> {
        val asignaciones = usuarioAsignaturaRepository.findAlumnosByAsignatura(asignaturaId)
        return asignaciones.map { toDto(it) }
    }

    @Transactional
    fun desactivarAsignacion(usuarioId: UUID, asignaturaId: UUID): UsuarioAsignaturaDto {
        val asignacion = usuarioAsignaturaRepository.findByUsuarioIdAndAsignaturaId(usuarioId, asignaturaId)
            ?: throw IllegalArgumentException("No existe asignación entre usuario $usuarioId y asignatura $asignaturaId")

        asignacion.activo = false
        val asignacionActualizada = usuarioAsignaturaRepository.save(asignacion)
        return toDto(asignacionActualizada)
    }

    @Transactional
    fun reactivarAsignacion(usuarioId: UUID, asignaturaId: UUID): UsuarioAsignaturaDto {
        val asignacion = usuarioAsignaturaRepository.findByUsuarioIdAndAsignaturaId(usuarioId, asignaturaId)
            ?: throw IllegalArgumentException("No existe asignación entre usuario $usuarioId y asignatura $asignaturaId")

        asignacion.activo = true
        val asignacionActualizada = usuarioAsignaturaRepository.save(asignacion)
        return toDto(asignacionActualizada)
    }

    @Transactional
    fun eliminarAsignacion(usuarioId: UUID, asignaturaId: UUID) {
        val asignacion = usuarioAsignaturaRepository.findByUsuarioIdAndAsignaturaId(usuarioId, asignaturaId)
            ?: throw IllegalArgumentException("No existe asignación entre usuario $usuarioId y asignatura $asignaturaId")

        usuarioAsignaturaRepository.delete(asignacion)
    }

    private fun toDto(asignacion: UsuarioAsignatura): UsuarioAsignaturaDto {
        val nombreCompleto = "${asignacion.usuario.firstName} ${asignacion.usuario.lastName}"
        // val rolUsuarioNombre = asignacion.usuario.role.name
        
        return UsuarioAsignaturaDto(
            id = asignacion.id!!,
            usuarioId = asignacion.usuario.id!!,
            usuarioNombre = nombreCompleto,
            asignaturaId = asignacion.asignatura.id!!,
            asignaturaNombre = asignacion.asignatura.name,
            fechaAsignacion = asignacion.fechaAsignacion,
            activo = asignacion.activo,
            rolUsuario = "temp" // rolUsuarioNombre
        )
    }
}
