package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.dto.AssignSubjectDto
import org.duocuc.capstonebackend.dto.SimpleSubjectDto
import org.duocuc.capstonebackend.dto.UserSubjectDto
import org.duocuc.capstonebackend.service.UserSubjectService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * REST controller to manage user <> subject assignments.
 *
 * Available endpoints:
 * - GET /api/subjects/professor/{id} - list professor's subjects
 * - GET /api/subjects/student/{id} - list student's subjects
 * - POST /api/admin/subjects/assign - assign subject to user (ADMIN)
 * - GET /api/subjects/{id}/professors - list professors for a subject (ADMIN)
 * - GET /api/subjects/{id}/students - list students for a subject (PROFESSOR/ADMIN)
 * - PUT /api/admin/subjects/deactivate - deactivate assignment (ADMIN)
 * - PUT /api/admin/subjects/reactivate - reactivate assignment (ADMIN)
 * - DELETE /api/admin/subjects/delete - delete assignment (ADMIN)
 */
 
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["http://localhost:5173", "http://localhost:3000"])
class UserSubjectController(
    private val userSubjectService: UserSubjectService
) {

    @GetMapping("/subjects/professor/{professorId}")
    fun getProfessorSubjects(@PathVariable professorId: UUID): ResponseEntity<List<SimpleSubjectDto>> {
        return try {
            val subjects = userSubjectService.getProfessorSubjects(professorId)
            ResponseEntity.ok(subjects)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/subjects/student/{studentId}")
    fun getStudentSubjects(@PathVariable studentId: UUID): ResponseEntity<List<SimpleSubjectDto>> {
        return try {
            val subjects = userSubjectService.getStudentSubjects(studentId)
            ResponseEntity.ok(subjects)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/admin/subjects/assign")
    @PreAuthorize("hasRole('admin')")
    fun assignSubject(@RequestBody dto: AssignSubjectDto): ResponseEntity<Any> {
        return try {
            val assignment = userSubjectService.assignSubject(dto)
            ResponseEntity.status(HttpStatus.CREATED).body(assignment)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @GetMapping("/subjects/{subjectId}/professors")
    @PreAuthorize("hasRole('admin')")
    fun getProfessorsForSubject(@PathVariable subjectId: UUID): ResponseEntity<List<UserSubjectDto>> {
        val professors = userSubjectService.getProfessorsForSubject(subjectId)
        return ResponseEntity.ok(professors)
    }

    @GetMapping("/subjects/{subjectId}/students")
    @PreAuthorize("hasAnyRole('professor', 'admin')")
    fun getStudentsForSubject(@PathVariable subjectId: UUID): ResponseEntity<List<UserSubjectDto>> {
        val students = userSubjectService.getStudentsForSubject(subjectId)
        return ResponseEntity.ok(students)
    }

    @PutMapping("/admin/subjects/deactivate")
    @PreAuthorize("hasRole('admin')")
    fun deactivateAssignment(
        @RequestParam userId: UUID,
        @RequestParam subjectId: UUID
    ): ResponseEntity<Any> {
        return try {
            val assignment = userSubjectService.deactivateAssignment(userId, subjectId)
            ResponseEntity.ok(assignment)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @PutMapping("/admin/subjects/reactivate")
    @PreAuthorize("hasRole('admin')")
    fun reactivateAssignment(
        @RequestParam userId: UUID,
        @RequestParam subjectId: UUID
    ): ResponseEntity<Any> {
        return try {
            val assignment = userSubjectService.reactivateAssignment(userId, subjectId)
            ResponseEntity.ok(assignment)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @DeleteMapping("/admin/subjects/delete")
    @PreAuthorize("hasRole('admin')")
    fun deleteAssignment(
        @RequestParam userId: UUID,
        @RequestParam subjectId: UUID
    ): ResponseEntity<Any> {
        return try {
            userSubjectService.deleteAssignment(userId, subjectId)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }
}
