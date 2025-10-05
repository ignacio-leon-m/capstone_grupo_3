package org.duocuc.capstonebackend.service

import org.duocuc.capstonebackend.dto.StudentRequestDto
import org.duocuc.capstonebackend.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService (
    private val userRepository: UserRepository,
){
    fun getAllStudents(): List<StudentRequestDto> {
        val students = userRepository.getUsersByRoleName("alumno")
        return students.map { user ->
            StudentRequestDto(
                name = user.firstName,
                lastName = user.lastName,
                email = user.email,
                lastLoginAt = user.lastLoginAt,
            )
        }
    }
}