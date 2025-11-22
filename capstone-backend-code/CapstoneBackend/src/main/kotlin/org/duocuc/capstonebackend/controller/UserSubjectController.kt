package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.dto.EnrollSubjectDto
import org.duocuc.capstonebackend.dto.SubjectInfoDto
import org.duocuc.capstonebackend.dto.UserSubjectDto
import org.duocuc.capstonebackend.service.UserSubjectService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["http://localhost:5173", "http://localhost:3000", "https://brainboost-static.netlify.app"])
class UserSubjectController(
    private val userSubjectService: UserSubjectService
) {
    // Get professor subjects
    @GetMapping("/subjects/professor/{professorId}")
    fun findProfessorSubjects(@PathVariable professorId: UUID): ResponseEntity<List<SubjectInfoDto>> {
        return try {
            val subjects = userSubjectService.getProfessorSubjects(professorId)
            ResponseEntity.ok(subjects)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }
    // Get student subjects
    @GetMapping("/subjects/student/{studentId}")
    fun findStudentSubjects(@PathVariable studentId: UUID): ResponseEntity<List<SubjectInfoDto>> {
        return try {
            val subjects = userSubjectService.getStudentSubjects(studentId)
            ResponseEntity.ok(subjects)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    // Enroll user to subject
    @PostMapping("/admin/subjects/enroll")
    @PreAuthorize("hasAuthority('admin')")
    fun enrollUserToSubject(@RequestBody dto: EnrollSubjectDto): ResponseEntity<Any> {
        return try {
            val enrollment = userSubjectService.enrollToSubject(dto)
            ResponseEntity.status(HttpStatus.CREATED).body(enrollment)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    // Get students of a subject
    @GetMapping("/subjects/{subjectId}/students")
    @PreAuthorize("hasAnyAuthority('profesor', 'admin')")
    fun findStudentsOfSubject(@PathVariable subjectId: UUID): ResponseEntity<List<UserSubjectDto>> {
        val students = userSubjectService.getStudentsOfSubject(subjectId)
        return ResponseEntity.ok(students)
    }

    // Deactivate a relationship between user/subject
    @PutMapping("/admin/subjects/deactivate")
    @PreAuthorize("hasAuthority('admin')")
    fun deactivateEnrollment(
        @RequestParam userId: UUID,
        @RequestParam subjectId: UUID
    ): ResponseEntity<Any> {
        return try {
            val enrollment = userSubjectService.deactivateEnrollment(userId, subjectId)
            ResponseEntity.ok(enrollment)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    // Reactivate a relationship between user/subject
    @PutMapping("/admin/subjects/reactivate")
    @PreAuthorize("hasAuthority('admin')")
    fun reactivateEnrollment(
        @RequestParam userId: UUID,
        @RequestParam subjectId: UUID
    ): ResponseEntity<Any> {
        return try {
            val enrollment = userSubjectService.reactivateEnrollment(userId, subjectId)
            ResponseEntity.ok(enrollment)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

}
