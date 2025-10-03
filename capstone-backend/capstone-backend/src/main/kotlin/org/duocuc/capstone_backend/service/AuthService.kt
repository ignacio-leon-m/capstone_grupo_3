package org.duocuc.capstone_backend.service

import jakarta.servlet.http.HttpServletRequest
import org.duocuc.capstone_backend.dto.LoginRequestDto
import org.duocuc.capstone_backend.dto.RegisterRequestDto
import org.duocuc.capstone_backend.dto.UserResponseDto
import org.duocuc.capstone_backend.model.Role
import org.duocuc.capstone_backend.model.User
import org.duocuc.capstone_backend.repository.RoleRepository
import org.duocuc.capstone_backend.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager
) {

    fun register(request: RegisterRequestDto): UserResponseDto {
        require(!userRepository.existsByEmail(request.email)) { "El correo ya está registrado" }

        val role = roleRepository.findByName("ROLE_USER")
            .orElseGet { roleRepository.save(Role(name = "ROLE_USER")) }

        val user = User(
            firstName = request.name,
            lastName = request.lastName,
            role = role,
            email = request.email,
            state = true,
            phone = request.phone,
            passwordHash = passwordEncoder.encode(request.password),
            createdAt = LocalDateTime.now()
        )

        val saved = userRepository.save(user)
        return UserResponseDto(
            id = saved.id,
            name = saved.firstName,
            lastName = saved.lastName,
            email = saved.email,
            role = saved.role
        )
    }

    fun login(request: LoginRequestDto): User {
        val token = UsernamePasswordAuthenticationToken(request.email, request.password)
        val auth = authenticationManager.authenticate(token)
        SecurityContextHolder.getContext().authentication = auth

        val user = userRepository.findByEmail(request.email).orElseThrow()
        user.lastLoginAt = LocalDateTime.now()
        return userRepository.save(user)
    }

    fun logout(request: HttpServletRequest) {
        request.getSession(false)?.invalidate()
        SecurityContextHolder.clearContext()
    }
}
