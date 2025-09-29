package org.duocuc.capstonebackend.service

import org.duocuc.capstonebackend.repository.RoleRepository
import org.duocuc.capstonebackend.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.aspectj.weaver.loadtime.Options
import org.duocuc.capstonebackend.dto.RegisterRequestDto
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.Optional

class AuthServiceTest {
    private val userRepository: UserRepository = mockk()
    private val roleRepository: RoleRepository = mockk()
    private val passwordEncoder: PasswordEncoder = mockk()


    private val authService: AuthService = AuthService(
        userRepository = userRepository,
        roleRepository = roleRepository,
        passwordEncoder = passwordEncoder
    )

    @Test
    fun userRegistryExceptionIfUserExists() {
        val register = RegisterRequestDto("nombre", "apellido", "donotto@gmail.com", "123456", "abc1234")
        every { userRepository.findByEmail(register.email)} returns Optional.of(mockk())

        assertThrows<IllegalStateException> {
            authService.userRegistry(register)
        }
    }

    @Test
    fun registerStudentFromExcel() {
    }

    @Test
    fun userResponse() {
    }

    @Test
    fun userLogin() {
    }

}