package org.duocuc.capstonebackend.service

import jakarta.persistence.EntityNotFoundException
import org.duocuc.capstonebackend.dto.SaveUserDto
import org.duocuc.capstonebackend.model.User
import org.duocuc.capstonebackend.repository.RoleRepository
import org.duocuc.capstonebackend.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository): UserDetailsService {
        override fun loadUserByUsername(email: String): UserDetails {
            val user = userRepository.findByEmail(email)
                .orElseThrow { UsernameNotFoundException("User not found with email: $email") }
            return org.springframework.security.core.userdetails.User(
                user.email,
                user.passwordHash,
                listOf(SimpleGrantedAuthority()) // add roles
            )
        }
}