package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.dto.CustomUserResponseDto
import org.duocuc.capstonebackend.dto.StudentRequestDto
import org.duocuc.capstonebackend.dto.UserResponseDto
import org.duocuc.capstonebackend.security.CustomUserDetails
import org.duocuc.capstonebackend.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/users")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/students")
    fun getAllStudents(): List<StudentRequestDto> {
        val students = userService.getAllStudents()
        return students
    }

    @GetMapping("/professors")
    fun getAllProfessors(): List<StudentRequestDto> {
        val professors = userService.getAllProfessors()
        return professors
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    fun me(@AuthenticationPrincipal user: CustomUserDetails): ResponseEntity<CustomUserResponseDto> {
        val role = user.authorities.firstOrNull()?.authority ?: "NO_ROLE"

        return ResponseEntity.ok(
            CustomUserResponseDto(
                id = user.id,
                email = user.email,
                role = role
            )
        )
    }
}