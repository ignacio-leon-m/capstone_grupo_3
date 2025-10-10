package org.duocuc.capstonebackend.service

import org.duocuc.capstonebackend.service.JwtTokenService
import org.duocuc.capstonebackend.dto.LoginRequestDto
import org.duocuc.capstonebackend.dto.LoginResponseDto
import org.duocuc.capstonebackend.dto.RegisterRequestDto
import org.duocuc.capstonebackend.dto.UserResponseDto
import org.duocuc.capstonebackend.model.User
import org.duocuc.capstonebackend.repository.RoleRepository
import org.duocuc.capstonebackend.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Suppress("SpellCheckingInspection")
@Service
class AuthService (
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: CustomUserDetailsService,
    private val jwtTokenService: JwtTokenService
) {
    private val log = LoggerFactory.getLogger(AuthService::class.java)

    fun userRegistry(request: RegisterRequestDto): UserResponseDto {
        if(userRepository.findByEmail(request.email).isPresent){
            throw IllegalStateException("Ya existe el usuario")
        }

        val role = roleRepository.findByName("profesor") ?: throw IllegalStateException("Rol por defecto no existe")

        val newUser = User(
            firstName = request.name,
            lastName = request.lastName,
            role = role,
            passwordHash = passwordEncoder.encode(request.password),
            email = request.email,
            state = true,
            phone = request.phone,
            createdAt = LocalDateTime.now(),
            lastLoginAt = null
            )

        val savedUser = userRepository.save(newUser)
        return userResponse(savedUser)
    }

    fun registerStudentFromExcel(request: RegisterRequestDto) {
        if (userRepository.findByEmail(request.email).isPresent) {
            log.info("Usuario con email ${request.email} ya existe. Omitiendo creación.")
            return
        }

        val role = roleRepository.findByName("alumno") ?: throw IllegalStateException("El rol 'alumno' no existe.")

        val newUser = User(
            firstName = request.name,
            lastName = request.lastName,
            role = role,
            passwordHash = passwordEncoder.encode(request.password),
            email = request.email,
            state = true,
            phone = request.phone,
            createdAt = LocalDateTime.now(),
            lastLoginAt = null
        )

        userRepository.save(newUser)
        log.info("Nuevo alumno creado con email: ${request.email}")
    }

    fun userResponse(user: User): UserResponseDto {
        return UserResponseDto(
            id = user.id,
            name = user.firstName,
            lastName = user.lastName,
            role = user.role,
            email = user.email,
        )
    }


    fun login(request: LoginRequestDto): LoginResponseDto {
        // 1) autenticar credenciales con el AuthenticationManager
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.email, request.password)
        )
        // 2) cargar UserDetails para generar token con authorities
        val userDetails = userDetailsService.loadUserByUsername(request.email)
        val token = jwtTokenService.generateToken(userDetails)
        // 3) actualizar lastLoginAt
        val user = userRepository.findByEmail(request.email).orElseThrow()
        user.lastLoginAt = LocalDateTime.now()
        userRepository.save(user)
        return LoginResponseDto(accessToken = token, role = user.role.name)
    }

}