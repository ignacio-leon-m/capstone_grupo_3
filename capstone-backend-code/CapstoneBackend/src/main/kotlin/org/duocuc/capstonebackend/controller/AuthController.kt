package org.duocuc.capstonebackend.controller

import jakarta.servlet.http.HttpServletRequest
import org.duocuc.capstonebackend.dto.LoginRequestDto
import org.duocuc.capstonebackend.dto.LoginResponseDto
import org.duocuc.capstonebackend.dto.RegisterRequestDto
import org.duocuc.capstonebackend.dto.UserResponseDto
import org.duocuc.capstonebackend.service.AuthService
import org.duocuc.capstonebackend.service.CustomUserDetailsService
import org.duocuc.capstonebackend.service.JwtTokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController (
    private val authService: AuthService,
    private val jwtTokenService: JwtTokenService,
    private val authenticationManager: AuthenticationManager,
    private val customUserDetailsService: CustomUserDetailsService
) {


    @PostMapping("/register")
    @PreAuthorize("hasAuthority('admin')")
    fun register(@RequestBody request: RegisterRequestDto): ResponseEntity<UserResponseDto> {
        val userResponse = authService.userRegistry(request)
        return ResponseEntity(userResponse, HttpStatus.CREATED)
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequestDto): ResponseEntity<*> {
        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.email, request.password)
            )
        } catch (e: BadCredentialsException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("message" to "Credenciales incorrectas"))
        }
        val userDetails = customUserDetailsService.loadUserByUsername(request.email)
        val token: String = jwtTokenService.generateToken(userDetails)
        
        // Modificado: Se obtiene el rol directamente (sin prefijo) y se asume que siempre existe.
        val role: String = userDetails.authorities.map { it.authority }.first()

        return ResponseEntity(LoginResponseDto(token, role), HttpStatus.OK)
    }

    @PostMapping("/logout")
    fun logout(request: HttpServletRequest): ResponseEntity<String> {
        val session = request.getSession(false)
        session?.invalidate() // Invalidate the session if it exists
        return ResponseEntity("Logout exitoso", HttpStatus.OK)
    }
}