package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.dto.EnrollSubjectDto
import org.duocuc.capstonebackend.dto.SubjectInfoDto
import org.duocuc.capstonebackend.dto.UserSubjectDto
import org.duocuc.capstonebackend.security.CurrentUser   // ⬅️ NUEVO
import org.duocuc.capstonebackend.repository.UserRepository // ⬅️ NUEVO
import org.duocuc.capstonebackend.service.UserSubjectService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["http://localhost:5173", "http://localhost:3000"])
class UserSubjectController(
    private val userSubjectService: UserSubjectService,
    private val currentUser: CurrentUser,         // ⬅️ NUEVO
    private val userRepository: UserRepository    // ⬅️ NUEVO
) {
    // -------- existentes por usuario (dejan SubjectInfoDto) --------

    // Get professor subjects (OK con el service actual)
    @GetMapping("/subjects/professor/{professorId}")
    fun findProfessorSubjects(@PathVariable professorId: UUID): ResponseEntity<List<SubjectInfoDto>> =
        try {
            ResponseEntity.ok(userSubjectService.getProfessorSubjects(professorId))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }

    // Get student subjects (OK con el service actual)
    @GetMapping("/subjects/student/{studentId}")
    fun findStudentSubjects(@PathVariable studentId: UUID): ResponseEntity<List<SubjectInfoDto>> =
        try {
            ResponseEntity.ok(userSubjectService.getStudentSubjects(studentId))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }

    // ----------------- NUEVO: mis asignaturas (sin UUID) -----------------
    @GetMapping("/me/subjects")
    @PreAuthorize("hasAnyAuthority('alumno','profesor','admin')")
    fun findMySubjects(): ResponseEntity<List<SubjectInfoDto>> {
        val meId = currentUser.id()
        val me = userRepository.findById(meId).orElseThrow()
        val role = me.role.name.lowercase()

        val subjects = when (role) {
            "alumno"   -> userSubjectService.getStudentSubjects(meId)
            "profesor" -> userSubjectService.getProfessorSubjects(meId)
            "admin"    -> emptyList() // o podrías devolver todas si lo defines en tu service
            else       -> emptyList()
        }
        return ResponseEntity.ok(subjects)
    }

    /** Inscribe un usuario (admin) */
    @PostMapping("/admin/subjects/enroll")
    @PreAuthorize("hasAuthority('admin')")
    fun enrollUserToSubject(@RequestBody dto: EnrollSubjectDto): ResponseEntity<Any> =
        try {
            val enrollment = userSubjectService.enrollToSubject(dto)
            ResponseEntity.status(HttpStatus.CREATED).body(enrollment)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body(mapOf("error" to "Error interno al inscribir usuario."))
        }

    /**
     * Estudiantes de un ramo (profesor o admin)
     */
    @GetMapping("/subjects/{subjectId}/students")
    @PreAuthorize("hasAnyAuthority('profesor','admin')")
    fun findStudentsOfSubject(@PathVariable subjectId: UUID): ResponseEntity<List<UserSubjectDto>> =
        try {
            ResponseEntity.ok(userSubjectService.getStudentsOfSubject(subjectId))
        } catch (e: Exception) {
            ResponseEntity.internalServerError().build()
        }

    /**
     * Profesores de un ramo (admin)
     */
    @GetMapping("/subjects/{subjectId}/professor")
    @PreAuthorize("hasAuthority('admin')")
    fun findProfessorOfSubject(@PathVariable subjectId: UUID): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
            .body(mapOf("error" to "No implementado en el servicio. Agrega getProfessorsOfSubject(subjectId)."))

    /** Desactivar inscripción (admin) */
    @PutMapping("/admin/subjects/deactivate")
    @PreAuthorize("hasAuthority('admin')")
    fun deactivateEnrollment(
        @RequestParam userId: UUID,
        @RequestParam subjectId: UUID
    ): ResponseEntity<Any> =
        try {
            ResponseEntity.ok(userSubjectService.deactivateEnrollment(userId, subjectId))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body(mapOf("error" to "Error interno al desactivar inscripción."))
        }

    /** Reactivar inscripción (admin) */
    @PutMapping("/admin/subjects/reactivate")
    @PreAuthorize("hasAuthority('admin')")
    fun reactivateEnrollment(
        @RequestParam userId: UUID,
        @RequestParam subjectId: UUID
    ): ResponseEntity<Any> =
        try {
            ResponseEntity.ok(userSubjectService.reactivateEnrollment(userId, subjectId))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body(mapOf("error" to "Error interno al reactivar inscripción."))
        }
}
