package org.duocuc.capstonebackend.service

import org.duocuc.capstonebackend.dto.LoginRequestDto
import org.duocuc.capstonebackend.dto.RegisterRequestDto
import org.duocuc.capstonebackend.dto.UserResponseDto
import org.duocuc.capstonebackend.model.User
import org.duocuc.capstonebackend.repository.RoleRepository
import org.duocuc.capstonebackend.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Suppress("SpellCheckingInspection")
@Service
class AuthService (
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    fun userRegistry(request: RegisterRequestDto): UserResponseDto {
        if(userRepository.findByEmail(request.email).isPresent){
            throw IllegalStateException("Ya existe el usuario")
        }

        val role = roleRepository.findById(UUID.fromString("22d58bf8-c5fe-47f2-9540-ebbf5e411c78"))
            .orElseThrow { IllegalStateException("Rol por defecto no existe") }

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

    fun userResponse(user: User): UserResponseDto {
        return UserResponseDto(
            id = user.id,
            name = user.firstName,
            lastName = user.lastName,
            role = user.role,
            email = user.email,
        )
    }


    fun userLogin(request: LoginRequestDto): User {
        val user = userRepository.findByEmail(request.email)
            .orElseThrow { IllegalArgumentException("Correo o contraseña inválidos") }

        if(!passwordEncoder.matches(request.password, user.passwordHash)){
            throw IllegalArgumentException("Correo o contraseña inválidos")
        }

        user.lastLoginAt = LocalDateTime.now()
        userRepository.save(user)

        return user
    }

}