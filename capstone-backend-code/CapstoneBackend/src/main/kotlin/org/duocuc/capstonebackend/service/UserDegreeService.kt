package org.duocuc.capstonebackend.service

import org.duocuc.capstonebackend.dto.AssignSubjectDto
import org.duocuc.capstonebackend.dto.SimpleSubjectDto
import org.duocuc.capstonebackend.dto.UserSubjectDto
import org.duocuc.capstonebackend.model.UserSubject
import org.duocuc.capstonebackend.repository.SubjectRepository
import org.duocuc.capstonebackend.repository.UserRepository
import org.duocuc.capstonebackend.repository.UserSubjectRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * Service responsible for managing relationships between users and subjects.
 * All variable names and comments are in English.
 */
@Service
class UserSubjectService(
    private val userSubjectRepository: UserSubjectRepository,
    private val userRepository: UserRepository,
    private val subjectRepository: SubjectRepository
) {

    @Transactional
    fun assignSubject(dto: AssignSubjectDto): UserSubjectDto {
        if (userSubjectRepository.existsByUserIdAndSubjectId(dto.userId, dto.subjectId)) {
            throw IllegalArgumentException("User is already assigned to this subject")
        }

        val user = userRepository.findById(dto.userId)
            .orElseThrow { IllegalArgumentException("User not found with id: ${dto.userId}") }

        val subject = subjectRepository.findById(dto.subjectId)
            .orElseThrow { IllegalArgumentException("Subject not found with id: ${dto.subjectId}") }

        val newAssignment = UserSubject(
            user = user,
            subject = subject
        )

        val savedAssignment = userSubjectRepository.save(newAssignment)
        return toDto(savedAssignment)
    }

    /**
     * Returns the list of subjects assigned to a professor.
     */
    fun getProfessorSubjects(professorId: UUID): List<SimpleSubjectDto> {
        val user = userRepository.findById(professorId)
            .orElseThrow { IllegalArgumentException("User not found with id: $professorId") }

        val assignments = userSubjectRepository.findByUserId(professorId)
        return assignments.map { assignment ->
            SimpleSubjectDto(
                id = assignment.subject.id!!,
                name = assignment.subject.name,
                assignmentDate = assignment.assignDate,
                active = assignment.active
            )
        }
    }

    /**
     * Returns the list of subjects assigned to a student.
     */
    fun getStudentSubjects(studentId: UUID): List<SimpleSubjectDto> {
        userRepository.findById(studentId)
            .orElseThrow { IllegalArgumentException("User not found with id: $studentId") }

        val assignments = userSubjectRepository.findByUserId(studentId)
        return assignments.map { assignment ->
            SimpleSubjectDto(
                id = assignment.subject.id!!,
                name = assignment.subject.name,
                assignmentDate = assignment.assignDate,
                active = assignment.active
            )
        }
    }

    fun getProfessorsForSubject(subjectId: UUID): List<UserSubjectDto> {
        val assignments = userSubjectRepository.findProfessorsBySubject(subjectId)
        return assignments.map { toDto(it) }
    }

    fun getStudentsForSubject(subjectId: UUID): List<UserSubjectDto> {
        val assignments = userSubjectRepository.findStudentsBySubject(subjectId)
        return assignments.map { toDto(it) }
    }

    @Transactional
    fun deactivateAssignment(userId: UUID, subjectId: UUID): UserSubjectDto {
        val assignment = userSubjectRepository.findByUserIdAndSubjectId(userId, subjectId)
            ?: throw IllegalArgumentException("No assignment exists between user $userId and subject $subjectId")

        assignment.active = false
        val updated = userSubjectRepository.save(assignment)
        return toDto(updated)
    }

    @Transactional
    fun reactivateAssignment(userId: UUID, subjectId: UUID): UserSubjectDto {
        val assignment = userSubjectRepository.findByUserIdAndSubjectId(userId, subjectId)
            ?: throw IllegalArgumentException("No assignment exists between user $userId and subject $subjectId")

        assignment.active = true
        val updated = userSubjectRepository.save(assignment)
        return toDto(updated)
    }

    @Transactional
    fun deleteAssignment(userId: UUID, subjectId: UUID) {
        val assignment = userSubjectRepository.findByUserIdAndSubjectId(userId, subjectId)
            ?: throw IllegalArgumentException("No assignment exists between user $userId and subject $subjectId")

        userSubjectRepository.delete(assignment)
    }

    private fun toDto(assignment: UserSubject): UserSubjectDto {
        val fullName = "${assignment.user.firstName} ${assignment.user.lastName}"

        return UserSubjectDto(
            id = assignment.id!!,
            userId = assignment.user.id!!,
            userName = fullName,
            subjectId = assignment.subject.id!!,
            subjectName = assignment.subject.name,
            assignDate = assignment.assignDate,
            active = assignment.active,
            userRole = assignment.user.role.name
        )
    }
}
