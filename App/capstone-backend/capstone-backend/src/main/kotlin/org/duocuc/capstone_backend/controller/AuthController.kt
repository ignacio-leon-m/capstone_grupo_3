package org.duocuc.capstone_backend.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.duocuc.capstone_backend.dto.LoginRequestDto
import org.duocuc.capstone_backend.dto.RegisterRequestDto
import org.duocuc.capstone_backend.dto.UserResponseDto
import org.duocuc.capstone_backend.model.User
import org.duocuc.capstone_backend.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val securityContextRepository: SecurityContextRepository
) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequestDto): ResponseEntity<UserResponseDto> {
        val userResponse = authService.register(request)
        return ResponseEntity(userResponse, HttpStatus.CREATED)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginRequestDto,
        httpReq: HttpServletRequest,
        httpRes: HttpServletResponse
    ): ResponseEntity<User> {
        val user = authService.login(request)           // aquí se hace authenticate() y se setea el SecurityContextHolder
        httpReq.getSession(true)                        // asegura sesión
        val context = SecurityContextHolder.getContext()
        securityContextRepository.saveContext(context, httpReq, httpRes) // <- guarda en JSESSIONID
        return ResponseEntity.ok(user)
    }

    @PostMapping("/logout")
    fun logout(httpReq: HttpServletRequest): ResponseEntity<String> {
        authService.logout(httpReq)
        return ResponseEntity.ok("Logout exitoso")
    }
}
