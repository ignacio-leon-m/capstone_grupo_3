package org.duocuc.capstone_backend.controller

import org.duocuc.capstone_backend.dto.UserResponseDto
import org.duocuc.capstone_backend.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userRepository: UserRepository
) {
    @GetMapping("/me")
    fun me(auth: Authentication): ResponseEntity<UserResponseDto> {
        val email = auth.name
        val u = userRepository.findByEmail(email).orElseThrow()
        return ResponseEntity.ok(
            UserResponseDto(
                id = u.id,
                name = u.firstName,
                lastName = u.lastName,
                email = u.email,
                role = u.role
            )
        )
    }
}
