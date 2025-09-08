package org.duocuc.capstonebackend.service

import org.duocuc.capstonebackend.dto.LoginRequestDto
import org.duocuc.capstonebackend.dto.RegisterRequestDto
import org.duocuc.capstonebackend.dto.UserResponseDto
import org.duocuc.capstonebackend.model.User
import org.duocuc.capstonebackend.repository.RoleRepository
import org.duocuc.capstonebackend.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.Date

@Service
class AuthService (
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder,
    //private val authenticationManager: AuthenticationManager
) {
    fun userRegistry(request: RegisterRequestDto): UserResponseDto {
        if(userRepository.findByEmail(request.email).isPresent){
            throw IllegalStateException("Ya existe el usuario")
        }

        val role = roleRepository.findById(request.idRole)
            .orElseThrow{IllegalArgumentException("Rol Id inválido")}

        val newUser = User(
            firstName = request.name,
            lastName = request.lastName,
            idRole = role,
            passwordHash = passwordEncoder.encode(request.password),
            email = request.email,
            state = true,
            phone = request.phone,
            createdAt = Date(),
            lastLoginAt = null
            )

        val savedUser = userRepository.save(newUser)
        return mapToUserResponse(savedUser)
    }

    private fun mapToUserResponse(user: User): UserResponseDto{
        return UserResponseDto(
            id = user.id,
            name = user.firstName,
            lastName = user.lastName,
            email = user.email,
            role = user.idRole.name
        )
    }

    fun userLogin(request: LoginRequestDto): UserResponseDto {
        val user = userRepository.findByEmail(request.email)
            .orElseThrow { IllegalArgumentException("Correo o contraseña inválidos") }

        if(!passwordEncoder.matches(request.password, user.passwordHash)){
            throw IllegalArgumentException("Correo o contraseña inválidos")
        }

        user.lastLoginAt = Date()
        userRepository.save(user)

        return mapToUserResponse(user)
    }


}