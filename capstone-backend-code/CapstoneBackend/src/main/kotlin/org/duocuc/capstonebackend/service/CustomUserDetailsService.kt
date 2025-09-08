package org.duocuc.capstonebackend.service

import org.duocuc.capstonebackend.dto.LoginRequestDto
import org.duocuc.capstonebackend.dto.UserResponseDto
import org.duocuc.capstonebackend.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(private val userRepository: UserRepository): UserDetailsService {
        override fun loadUserByUsername(email: String): UserDetails {
            val user = userRepository.findByEmail(email)
                .orElseThrow { UsernameNotFoundException("User not found with email: $email") }
            return User(
                user.email,
                user.passwordHash,
                listOf(SimpleGrantedAuthority(user.idRole.name)))

        }
}