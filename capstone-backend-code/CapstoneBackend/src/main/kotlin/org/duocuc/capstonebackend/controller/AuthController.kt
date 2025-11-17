package org.duocuc.capstonebackend.controller

import jakarta.servlet.http.HttpServletRequest
import org.duocuc.capstonebackend.dto.LoginRequestDto
import org.duocuc.capstonebackend.dto.LoginResponseDto
import org.duocuc.capstonebackend.dto.RegisterRequestDto
import org.duocuc.capstonebackend.dto.UserResponseDto
import org.duocuc.capstonebackend.dto.MeDto
import org.duocuc.capstonebackend.repository.UserRepository
import org.duocuc.capstonebackend.service.AuthService
import org.duocuc.capstonebackend.service.CustomUserDetailsService
import org.duocuc.capstonebackend.service.JwtTokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val jwtTokenService: JwtTokenService,
    private val authenticationManager: AuthenticationManager,
    private val customUserDetailsService: CustomUserDetailsService,
    private val userRepository: UserRepository   // <<--- inyectado
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
        val role: String = userDetails.authorities.map { it.authority }.first()

        return ResponseEntity(LoginResponseDto(token, role), HttpStatus.OK)
    }

    @PostMapping("/logout")
    fun logout(request: HttpServletRequest): ResponseEntity<String> {
        val session = request.getSession(false)
        session?.invalidate()
        return ResponseEntity("Logout exitoso", HttpStatus.OK)
    }

    // ====== NUEVO: perfil del usuario autenticado ======
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    fun me(): ResponseEntity<MeDto> {
        val auth = SecurityContextHolder.getContext().authentication
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        val email = auth.name
        val user = userRepository.findByEmail(email).orElseThrow()

        val dto = MeDto(
            id = user.id!!,
            email = user.email,
            name = user.firstName,
            lastName = user.lastName,
            role = user.role.name
        )
        return ResponseEntity.ok(dto)
    }

}
