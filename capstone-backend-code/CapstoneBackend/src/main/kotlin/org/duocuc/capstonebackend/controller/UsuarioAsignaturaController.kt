package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.dto.AsignarAsignaturaDto
import org.duocuc.capstonebackend.dto.AsignaturaSimpleDto
import org.duocuc.capstonebackend.dto.UsuarioAsignaturaDto
import org.duocuc.capstonebackend.service.UsuarioAsignaturaService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * Controlador REST para gestionar asignaciones de usuarios a asignaturas.
 * 
 * Endpoints disponibles:
 * - GET /api/asignaturas/profesor/{id} - Obtener asignaturas de un profesor
 * - GET /api/asignaturas/alumno/{id} - Obtener asignaturas de un alumno
 * - POST /api/admin/asignaturas/asignar - Asignar asignatura a usuario (solo ADMIN)
 * - GET /api/asignaturas/{id}/profesores - Ver profesores de una asignatura (ADMIN)
 * - GET /api/asignaturas/{id}/alumnos - Ver alumnos de una asignatura (PROFESOR/ADMIN)
 * - PUT /api/admin/asignaturas/desactivar - Desactivar asignación (ADMIN)
 * - PUT /api/admin/asignaturas/reactivar - Reactivar asignación (ADMIN)
 * - DELETE /api/admin/asignaturas/eliminar - Eliminar asignación (ADMIN)
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["http://localhost:5173", "http://localhost:3000"]) // Ajustar según tu frontend
class UsuarioAsignaturaController(
    private val usuarioAsignaturaService: UsuarioAsignaturaService
) {

    /**
     * Obtiene todas las asignaturas de un profesor.
     * 
     * @param profesorId UUID del profesor
     * @return Lista de asignaturas asignadas al profesor
     */
    @GetMapping("/asignaturas/profesor/{profesorId}")
    fun obtenerAsignaturasProfesor(@PathVariable profesorId: UUID): ResponseEntity<List<AsignaturaSimpleDto>> {
        return try {
            val asignaturas = usuarioAsignaturaService.obtenerAsignaturasProfesor(profesorId)
            ResponseEntity.ok(asignaturas)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Obtiene todas las asignaturas de un alumno.
     * 
     * @param alumnoId UUID del alumno
     * @return Lista de asignaturas asignadas al alumno
     */
    @GetMapping("/asignaturas/alumno/{alumnoId}")
    fun obtenerAsignaturasAlumno(@PathVariable alumnoId: UUID): ResponseEntity<List<AsignaturaSimpleDto>> {
        return try {
            val asignaturas = usuarioAsignaturaService.obtenerAsignaturasAlumno(alumnoId)
            ResponseEntity.ok(asignaturas)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Asigna una asignatura a un usuario (profesor o alumno).
     * Solo accesible por usuarios con rol ADMIN.
     * 
     * @param dto Datos de la asignación (usuarioId, asignaturaId)
     * @return Detalle de la asignación creada
     */
    @PostMapping("/admin/asignaturas/asignar")
    @PreAuthorize("hasRole('admin')")
    fun asignarAsignatura(@RequestBody dto: AsignarAsignaturaDto): ResponseEntity<Any> {
        return try {
            val asignacion = usuarioAsignaturaService.asignarAsignatura(dto)
            ResponseEntity.status(HttpStatus.CREATED).body(asignacion)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    /**
     * Obtiene todos los profesores asignados a una asignatura.
     * Solo accesible por ADMIN.
     * 
     * @param asignaturaId UUID de la asignatura
     * @return Lista de profesores con sus asignaciones
     */
    @GetMapping("/asignaturas/{asignaturaId}/profesores")
    @PreAuthorize("hasRole('admin')")
    fun obtenerProfesoresDeAsignatura(@PathVariable asignaturaId: UUID): ResponseEntity<List<UsuarioAsignaturaDto>> {
        val profesores = usuarioAsignaturaService.obtenerProfesoresDeAsignatura(asignaturaId)
        return ResponseEntity.ok(profesores)
    }

    /**
     * Obtiene todos los alumnos asignados a una asignatura.
     * Accesible por PROFESOR y ADMIN.
     * 
     * @param asignaturaId UUID de la asignatura
     * @return Lista de alumnos con sus asignaciones
     */
    @GetMapping("/asignaturas/{asignaturaId}/alumnos")
    @PreAuthorize("hasAnyRole('profesor', 'admin')")
    fun obtenerAlumnosDeAsignatura(@PathVariable asignaturaId: UUID): ResponseEntity<List<UsuarioAsignaturaDto>> {
        val alumnos = usuarioAsignaturaService.obtenerAlumnosDeAsignatura(asignaturaId)
        return ResponseEntity.ok(alumnos)
    }

    /**
     * Desactiva una asignación (soft delete).
     * Solo accesible por ADMIN.
     * 
     * @param usuarioId UUID del usuario
     * @param asignaturaId UUID de la asignatura
     * @return Asignación actualizada
     */
    @PutMapping("/admin/asignaturas/desactivar")
    @PreAuthorize("hasRole('admin')")
    fun desactivarAsignacion(
        @RequestParam usuarioId: UUID,
        @RequestParam asignaturaId: UUID
    ): ResponseEntity<Any> {
        return try {
            val asignacion = usuarioAsignaturaService.desactivarAsignacion(usuarioId, asignaturaId)
            ResponseEntity.ok(asignacion)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    /**
     * Reactiva una asignación previamente desactivada.
     * Solo accesible por ADMIN.
     * 
     * @param usuarioId UUID del usuario
     * @param asignaturaId UUID de la asignatura
     * @return Asignación actualizada
     */
    @PutMapping("/admin/asignaturas/reactivar")
    @PreAuthorize("hasRole('admin')")
    fun reactivarAsignacion(
        @RequestParam usuarioId: UUID,
        @RequestParam asignaturaId: UUID
    ): ResponseEntity<Any> {
        return try {
            val asignacion = usuarioAsignaturaService.reactivarAsignacion(usuarioId, asignaturaId)
            ResponseEntity.ok(asignacion)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    /**
     * Elimina completamente una asignación (hard delete).
     * Solo accesible por ADMIN.
     * 
     * @param usuarioId UUID del usuario
     * @param asignaturaId UUID de la asignatura
     */
    @DeleteMapping("/admin/asignaturas/eliminar")
    @PreAuthorize("hasRole('admin')")
    fun eliminarAsignacion(
        @RequestParam usuarioId: UUID,
        @RequestParam asignaturaId: UUID
    ): ResponseEntity<Any> {
        return try {
            usuarioAsignaturaService.eliminarAsignacion(usuarioId, asignaturaId)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }
}
