package org.duocuc.capstonebackend.controller

import jakarta.servlet.http.HttpServletRequest
import org.duocuc.capstonebackend.dto.LoginRequestDto
import org.duocuc.capstonebackend.dto.RegisterRequestDto
import org.duocuc.capstonebackend.dto.UserResponseDto
import org.duocuc.capstonebackend.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController (private val authService: AuthService) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequestDto): ResponseEntity<UserResponseDto> {
        val userResponse = authService.userRegistry(request)
        return ResponseEntity(userResponse, HttpStatus.CREATED)
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequestDto): ResponseEntity<UserResponseDto> {
        val userResponse = authService.userLogin(request)
        return ResponseEntity.ok(userResponse) // when login is successful, Spring Security creates a session and returns a session cookie JSESSIONID to the client
    }

    @PostMapping("/logout")
    fun logout(request: HttpServletRequest): ResponseEntity<String> {
        val session = request.getSession(false)
        session?.invalidate() // Invalidate the session if it exists
        return ResponseEntity.ok("Logout exitoso")
    }


}