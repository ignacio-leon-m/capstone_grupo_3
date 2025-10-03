package org.duocuc.capstonebackend.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.duocuc.capstonebackend.dto.LoginRequestDto
import org.duocuc.capstonebackend.dto.RegisterRequestDto
import org.duocuc.capstonebackend.model.Role
import org.duocuc.capstonebackend.model.User
import org.duocuc.capstonebackend.repository.RoleRepository
import org.duocuc.capstonebackend.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

class AuthServiceUnitTest {
    private val userRepository: UserRepository = mockk(relaxed = true)
    private val roleRepository: RoleRepository = mockk()
    private val passwordEncoder: PasswordEncoder = mockk()
    private val service = AuthService(userRepository, roleRepository, passwordEncoder)

    fun `userRegistry creates an user with profesor role`() {
        val req = RegisterRequestDto(
            name = "Mick", lastName = "Jagger",
            email = "mr.jagger@duocuc.cl", phone = "123456", password = "abc1234"
        )
        every { userRepository.findByEmail(req.email) } returns Optional.empty()
        val roleProfessor = Role(name = "profesor")
        every { roleRepository.findByName("profesor") } returns roleProfessor
        every { passwordEncoder.encode(req.password) } returns "hashed-abc1234"

        var captured: User? = null
        every { userRepository.save(any()) } answers { firstArg<User>().also { captured = it } }

        val dto = service.userRegistry(req)

        verify(exactly = 1) { userRepository.findByEmail(req.email) }
        verify(exactly = 1) { roleRepository.findByName("profesor") }
        verify(exactly = 1) { passwordEncoder.encode(req.password) }
        verify(exactly = 1) { userRepository.save(any()) }

        val saved = captured!!
        assertEquals(req.email, saved.email)
        assertEquals("hashed-abc1234", saved.passwordHash)
        assertEquals(roleProfessor, saved.role)

        assertEquals(req.name, dto.name)
        assertEquals(req.lastName, dto.lastName)
        assertEquals(req.email, dto.email)
        assertEquals(roleProfessor, dto.role)
    }

    @Test
    fun `userLogin fails when password does not match`() {
        val login = LoginRequestDto(email = "user@duocuc.cl", password = "wrong")
        val role = Role(name = "alumno")
        val user = User(
            id = UUID.randomUUID(), firstName = "User", lastName = "Test",
            role = role, email = login.email, state = true, phone = null,
            passwordHash = "hashed-secret", createdAt = LocalDateTime.now(), lastLoginAt = null
        )
        // Acá lo que hace es simular que el usuario user existe en la bbdd y se 'salta' la lógica por detrás
        every { userRepository.findByEmail(login.email) } returns Optional.of(user)
        every { passwordEncoder.matches(login.password, user.passwordHash) } returns false
        // Si el password no hace match (cosa que estamos simulando) el servicio debe lanzar IllegalArgumentException
        assertThrows<IllegalArgumentException> { service.userLogin(login) }

        // Esta es la verificación de interacciones
        verify(exactly = 1) { userRepository.findByEmail(login.email) } // verifica que la búsqueda se hizo una pura vez
        verify(exactly = 1) { passwordEncoder.matches(login.password, user.passwordHash) } // que se compara 1 vez
        verify(exactly = 0) { userRepository.save(any()) } // que no se guarda ningún usuario y no hay efectos colaterales de persistencia
    }




<<<<<<< HEAD
}
=======
}
>>>>>>> feature/CG3-15
