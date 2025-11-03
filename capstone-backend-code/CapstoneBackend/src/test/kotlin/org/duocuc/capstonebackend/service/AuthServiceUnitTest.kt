package org.duocuc.capstonebackend.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.duocuc.capstonebackend.config.SecurityConfig
import org.duocuc.capstonebackend.dto.RegisterRequestDto
import org.duocuc.capstonebackend.model.Degree
import org.duocuc.capstonebackend.model.Institution
import org.duocuc.capstonebackend.model.Role
import org.duocuc.capstonebackend.model.User
import org.duocuc.capstonebackend.repository.DegreeRepository
import org.duocuc.capstonebackend.repository.RoleRepository
import org.duocuc.capstonebackend.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

class AuthServiceUnitTest {

    // --- Mocks ---
    private val userRepository: UserRepository = mockk(relaxed = true)
    private val roleRepository: RoleRepository = mockk()
    private val degreeRepository: DegreeRepository = mockk() // Mock añadido
    private val passwordEncoder: PasswordEncoder = mockk()
    private val securityConfig: SecurityConfig = mockk()
    private val authenticationManager: AuthenticationManager = mockk()
    private val customUserDetailsService: CustomUserDetailsService = mockk()
    private val jwtTokenService: JwtTokenService = mockk()

    // --- Service con todas las dependencias mockeadas ---
    private val service = AuthService(
        userRepository,
        roleRepository,
        degreeRepository, // Dependencia añadida
        securityConfig,
        authenticationManager,
        customUserDetailsService,
        jwtTokenService
    )

    @Test
    fun `userRegistry creates user with specified role and degree`() {
        // Arrange
        val req = RegisterRequestDto(
            name = "Mick",
            lastName = "Jagger",
            rut = "12345678-9",
            email = "mr.jagger@duocuc.cl",
            phone = "123456",
            password = "abc1234",
            role = "profesor",
            degreeName = "Ingeniería en Rock"
        )

        val mockRole = Role("profesor")
        mockRole.id = UUID.randomUUID()
        val mockInstitution = Institution("Rock University")
        mockInstitution.id = UUID.randomUUID()
        val mockDegree = Degree(id = UUID.randomUUID(), name = "Ingeniería en Rock", institution = mockInstitution)

        every { userRepository.findByEmail(req.email) } returns Optional.empty()
        every { securityConfig.passwordEncoder() } returns passwordEncoder
        every { passwordEncoder.encode(req.password) } returns "hashed-password"
        every { roleRepository.findByName(req.role!!.lowercase()) } returns mockRole
        every { degreeRepository.findByName(req.degreeName) } returns mockDegree // Mock para la carrera

        var capturedUser: User? = null
        every { userRepository.save(any()) } answers { firstArg<User>().also { capturedUser = it } }

        // Act
        val resultDto = service.userRegistry(req)

        // Assert
        verify(exactly = 1) { userRepository.findByEmail(req.email) }
        verify(exactly = 1) { roleRepository.findByName("profesor") }
        verify(exactly = 1) { degreeRepository.findByName("Ingeniería en Rock") }
        verify(exactly = 1) { userRepository.save(any()) }

        val savedUser = capturedUser!!
        assertEquals(req.name, savedUser.firstName)
        assertEquals(req.email, savedUser.email)
        assertEquals("hashed-password", savedUser.passwordHash)
        assertEquals("profesor", savedUser.role.name)
        assertEquals("Ingeniería en Rock", savedUser.degree.name) // Verificación de la carrera

        assertEquals(req.name, resultDto.name)
        assertEquals(req.email, resultDto.email)
        assertEquals(mockRole, resultDto.role)
    }

    @Test
    fun `userRegistry throws exception if user already exists`() {
        // Arrange
        val req = RegisterRequestDto("Test", "User", "12345678-9", "test@test.com", null, "pass", "alumno", "Carrera Test")
        every { userRepository.findByEmail(req.email) } returns Optional.of(mockk())

        // Act & Assert
        val exception = assertThrows<IllegalStateException> { service.userRegistry(req) }
        assertEquals("El usuario con el email '${req.email}' ya existe.", exception.message)
    }

    @Test
    fun `userRegistry throws exception if role is null or blank`() {
        // Arrange
        val req = RegisterRequestDto("Test", "User", "12345678-9", "test@test.com", null, "pass", "", "Carrera Test")
        
        // Act & Assert
        val exception = assertThrows<IllegalArgumentException> { service.userRegistry(req) }
        assertEquals("El rol no puede ser nulo o vacío.", exception.message)
    }

    @Test
    fun `userRegistry throws exception if degreeName is null or blank`() {
        // Arrange
        val req = RegisterRequestDto("Test", "User", "12345678-9", "test@test.com", null, "pass", "alumno", "")
        every { roleRepository.findByName("alumno") } returns mockk()

        // Act & Assert
        val exception = assertThrows<IllegalArgumentException> { service.userRegistry(req) }
        assertEquals("El nombre de la carrera no puede ser nulo o vacío.", exception.message)
    }

    @Test
    fun `userRegistry throws exception if degree does not exist`() {
        // Arrange
        val req = RegisterRequestDto("Test", "User", "12345678-9", "test@test.com", null, "pass", "alumno", "Carrera Inexistente")
        every { userRepository.findByEmail(req.email) } returns Optional.empty()
        every { roleRepository.findByName("alumno") } returns mockk()
        every { degreeRepository.findByName("Carrera Inexistente") } returns null // La carrera no se encuentra

        // Act & Assert
        val exception = assertThrows<IllegalStateException> { service.userRegistry(req) }
        assertEquals("La carrera 'Carrera Inexistente' no es válida o no existe.", exception.message)
    }
}