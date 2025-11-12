package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.dto.StudentRequestDto
import org.duocuc.capstonebackend.security.CurrentUser
import org.duocuc.capstonebackend.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/users")
class UserController (
    private val userService: UserService,
    private val currentUser: CurrentUser
){

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
    fun me(): ResponseEntity<Map<String, Any>> {
        val id = currentUser.id()
        val email = currentUser.email()
        val role = currentUser.role()
        return ResponseEntity.ok(mapOf(
            "id" to id,
            "email" to email,
            "role" to role
        ))
    }
}