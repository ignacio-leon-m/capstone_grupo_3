package org.duocuc.capstonebackend.service

import org.duocuc.capstonebackend.config.SecurityConfig
import org.duocuc.capstonebackend.dto.LoginRequestDto
import org.duocuc.capstonebackend.dto.LoginResponseDto
import org.duocuc.capstonebackend.dto.RegisterRequestDto
import org.duocuc.capstonebackend.dto.UserResponseDto
import org.duocuc.capstonebackend.model.User
import org.duocuc.capstonebackend.repository.DegreeRepository
import org.duocuc.capstonebackend.repository.RoleRepository
import org.duocuc.capstonebackend.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AuthService (
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val degreeRepository: DegreeRepository,
    private val securityConfig: SecurityConfig,
    private val authenticationManager: AuthenticationManager,
    private val customUserDetailsService: CustomUserDetailsService,
    private val jwtTokenService: JwtTokenService
) {
    private val log = LoggerFactory.getLogger(AuthService::class.java)

    fun userRegistry(request: RegisterRequestDto): UserResponseDto {
        // Verify if the user already exists
        if (userRepository.findByEmail(request.email).isPresent) {
            throw IllegalStateException("El usuario con el email '${request.email}' ya existe.")
        }
        // Verify by RUT
        if (userRepository.findByRut(request.rut).isPresent) {
            throw IllegalStateException("El usuario con el RUT '${request.rut}' ya existe.")
        }
        // Validate and find the Role
        val roleName = request.role.takeIf { it?.isNotBlank() ?: false }
            ?: throw IllegalArgumentException("El rol no puede ser nulo o vacío.")
        val role = roleRepository.findByName(roleName.lowercase()) 
            ?: throw IllegalStateException("El rol '${roleName}' no es válido o no existe.")

        val degreeName = "ing-informatica"
        val degree = degreeRepository.findByName(degreeName)
            ?: throw IllegalStateException("La carrera '${degreeName}' no es válida o no existe.")

        val newUser = User(
            firstName = request.name,
            lastName = request.lastName,
            rut = request.rut,
            role = role,
            degree = degree,
            passwordHash = securityConfig.passwordEncoder().encode(request.password),
            email = request.email,
            state = true,
            phone = request.phone,
            createdAt = LocalDateTime.now(),
            lastLoginAt = null
        )

        val savedUser = userRepository.save(newUser)
        return userResponse(savedUser)
    }

    fun registerStudentFromExcel(request: RegisterRequestDto): Boolean {
        // Verify if the user already exists by email
        if (userRepository.findByEmail(request.email).isPresent) {
            log.info("Usuario con email ${request.email} ya existe. Omitiendo creación.")
            return false
        }
        
        // Verify if the user already exists by RUT
        if (userRepository.findByRut(request.rut).isPresent) {
            log.info("Usuario con RUT ${request.rut} ya existe. Omitiendo creación.")
            return false
        }
        
        // Validate and find the Role
        val role = roleRepository.findByName("alumno") 
            ?: throw IllegalStateException("El rol 'alumno' no existe.")
        
        // Validate and find the Degree (Carrera)
        val degreeName = "ing-informatica"
        val degree = degreeRepository.findByName(degreeName)
            ?: throw IllegalStateException("La carrera '${degreeName}' no es válida o no existe.")

        val newUser = User(
            firstName = request.name,
            lastName = request.lastName,
            rut = request.rut,
            role = role,
            degree = degree,
            passwordHash = securityConfig.passwordEncoder().encode(request.password),
            email = request.email,
            state = true,
            phone = request.phone,
            createdAt = LocalDateTime.now(),
            lastLoginAt = null
        )

        userRepository.save(newUser)
        log.info("✓ Nuevo alumno creado: ${request.name} ${request.lastName} (${request.email})")
        return true
    }

    fun userResponse(user: User): UserResponseDto {
        return UserResponseDto(
            id = user.id,
            name = user.firstName,
            lastName = user.lastName,
            rut = user.rut,
            role = user.role,
            email = user.email
        )
    }

    fun login(request: LoginRequestDto): LoginResponseDto {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.email, request.password)
        )
        val userDetails = customUserDetailsService.loadUserByUsername(request.email)
        val token = jwtTokenService.generateToken(userDetails)
        val role = userDetails.authorities.map { it.authority }.first()
        val user = userRepository.findByEmail(request.email).orElseThrow()
        user.lastLoginAt = LocalDateTime.now()
        userRepository.save(user)
        return LoginResponseDto(token = token, role = role)
    }

}