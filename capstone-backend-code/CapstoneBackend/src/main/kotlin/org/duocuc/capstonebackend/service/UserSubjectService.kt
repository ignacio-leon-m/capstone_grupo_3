package org.duocuc.capstonebackend.service

import org.duocuc.capstonebackend.dto.UserSubjectDto
import org.duocuc.capstonebackend.dto.EnrollSubjectDto
import org.duocuc.capstonebackend.dto.SubjectInfoDto
import org.duocuc.capstonebackend.model.UserSubject
import org.duocuc.capstonebackend.repository.SubjectRepository
import org.duocuc.capstonebackend.repository.UserRepository
import org.duocuc.capstonebackend.repository.UserSubjectRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserSubjectService(
    private val userSubjectRepository: UserSubjectRepository,
    private val userRepository: UserRepository,
    private val subjectRepository: SubjectRepository
) {

    // Method used to enroll a subject
    @Transactional
    fun enrollToSubject(dto: EnrollSubjectDto): UserSubjectDto {
        // Search if there is a previous relationship between user and subject
        if (userSubjectRepository.existsByUserIdAndSubjectId(dto.userId, dto.subjectId)) {
            throw IllegalArgumentException("El usuario ya está asignado a esta asignatura")
        }
        // Find user by id
        val user = userRepository.findById(dto.userId)
            .orElseThrow { IllegalArgumentException("User no encontrado con id: ${dto.userId}") }
        // Find subject by id
        val subject = subjectRepository.findById(dto.subjectId)
            .orElseThrow { IllegalArgumentException("Subject no encontrada con id: ${dto.subjectId}") }

        val newEnrollment = UserSubject(
            user = user,
            subject = subject
        )
        // Save the relationship
        val savedEnrollment = userSubjectRepository.save(newEnrollment)
        return toDto(savedEnrollment)
    }
    
    // Method to get the professor's subjects
    @Transactional(readOnly = true)
    fun getProfessorSubjects(profesorId: UUID): List<SubjectInfoDto> {
        val user = userRepository.findByIdOrNull(profesorId)
        ?: throw IllegalArgumentException("Profesor no encontrado con id: $profesorId")

        if (!user.role.name.equals("profesor", ignoreCase = true)) {
            throw IllegalArgumentException("El usuario con id: $profesorId no es un profesor, sino un ${user.role.name}.")
        }
        val enrollments = userSubjectRepository.findSubjectByUserId(profesorId)

        // Map and return the result
        return enrollments.map { enrollment ->
            SubjectInfoDto(
                id = enrollment.subject.id!!,
                name = enrollment.subject.name,
                enrollmentDate = enrollment.enrollmentDate,
                active = enrollment.active
            )
        }
    }

    // Method to get the student's subjects
    @Transactional(readOnly = true)
    fun getStudentSubjects(studentId: UUID): List<SubjectInfoDto> {
        val user = userRepository.findByIdOrNull(studentId)
            ?: throw IllegalArgumentException("Profesor no encontrado con id: $studentId")

        if (!user.role.name.equals("alumno", ignoreCase = true)) {
            throw IllegalArgumentException("El usuario con id: $studentId no es un alumno, sino un ${user.role.name}.")
        }
        val enrollments = userSubjectRepository.findSubjectByUserId(studentId)

        // Map and return the result
        return enrollments.map { enrollment ->
            SubjectInfoDto(
                id = enrollment.subject.id!!,
                name = enrollment.subject.name,
                enrollmentDate = enrollment.enrollmentDate,
                active = enrollment.active
            )
        }
    }

    // Method to get all the students of a subject
    fun  getStudentsOfSubject(subjectId: UUID): List<UserSubjectDto> {
        val foundSubjectId = subjectRepository.findByIdOrNull(subjectId)?.id
            ?: throw IllegalArgumentException("Subject no existe.")
        val enrollments = userSubjectRepository.findStudentsBySubject(foundSubjectId)
        return enrollments.map { enrollment ->
            UserSubjectDto(
                id = enrollment.id!!,
                userId = enrollment.user.id!!,
                userName = "${enrollment.user.firstName} ${enrollment.user.lastName}",
                subjectId = enrollment.subject.id!!,
                subjectName = enrollment.subject.name,
                active = enrollment.active,
                userRole = enrollment.user.role.name,
                enrollmentDate = enrollment.enrollmentDate
            )
        }
    }

    // Method to turn off the relationship between user and subject
    @Transactional
    fun deactivateEnrollment(userId: UUID, subjectId: UUID): UserSubjectDto {
        val enrollment = userSubjectRepository.findByUserIdAndSubjectId(userId, subjectId)
            ?: throw IllegalArgumentException("No existe asignación entre user $userId y subject $subjectId")

        enrollment.active = false
        val updateEnrollment = userSubjectRepository.save(enrollment)
        return toDto(updateEnrollment)
    }

    // Method to turn on the relationship between a user and subjet
    @Transactional
    fun reactivateEnrollment(userId: UUID, subjectId: UUID): UserSubjectDto {
        val enrollment = userSubjectRepository.findByUserIdAndSubjectId(userId, subjectId)
            ?: throw IllegalArgumentException("No existe asignación entre user $userId y subject $subjectId")

        enrollment.active = true
        val updateEnrollment = userSubjectRepository.save(enrollment)
        return toDto(updateEnrollment)
    }

    private fun toDto(enrollment: UserSubject): UserSubjectDto {
        val fullName = "${enrollment.user.firstName} ${enrollment.user.lastName}"
        val userRoleName = enrollment.user.role.name
        
        return UserSubjectDto(
            id = enrollment.id!!,
            userId = enrollment.user.id!!,
            userName = fullName,
            subjectId = enrollment.subject.id!!,
            subjectName = enrollment.subject.name,
            enrollmentDate = enrollment.enrollmentDate,
            active = enrollment.active,
            userRole = userRoleName
        )
    }
}
