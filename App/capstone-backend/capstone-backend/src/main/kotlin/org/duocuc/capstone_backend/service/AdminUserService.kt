package org.duocuc.capstone_backend.service

import org.duocuc.capstone_backend.dto.CreateUserRequestDto
import org.duocuc.capstone_backend.dto.UserResponseDto
import org.duocuc.capstone_backend.model.User
import org.duocuc.capstone_backend.repository.RoleRepository
import org.duocuc.capstone_backend.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AdminUserService(
    private val userRepo: UserRepository,
    private val roleRepo: RoleRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun createUser(req: CreateUserRequestDto): UserResponseDto {
        require(!userRepo.existsByEmail(req.email)) { "El correo ya está registrado" }
        val role = roleRepo.findByName(req.roleName)
            .orElseThrow { IllegalArgumentException("Rol no encontrado: ${req.roleName}") }

        val u = User(
            firstName = req.name,
            lastName = req.lastName,
            role = role,
            email = req.email,
            state = true,
            phone = req.phone,
            passwordHash = passwordEncoder.encode(req.password),
            createdAt = LocalDateTime.now()
        )
        val saved = userRepo.save(u)

        return UserResponseDto(
            id = saved.id,
            name = saved.firstName,
            lastName = saved.lastName,
            email = saved.email,
            role = saved.role
        )
    }

    @Transactional(readOnly = true)
    fun listUsers(): List<UserResponseDto> =
        userRepo.findAll().map {
            UserResponseDto(
                id = it.id,
                name = it.firstName,
                lastName = it.lastName,
                email = it.email,
                role = it.role
            )
        }
}
