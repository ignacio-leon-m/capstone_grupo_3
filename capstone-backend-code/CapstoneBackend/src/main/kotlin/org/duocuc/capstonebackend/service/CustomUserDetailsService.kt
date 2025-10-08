package org.duocuc.capstonebackend.service

import org.duocuc.capstonebackend.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        // findByEmail returns Optional<User>;
        val user = userRepository.findByEmail(username)
            .orElseThrow { UsernameNotFoundException("Usuario no encontrado: $username") }
        val authority = SimpleGrantedAuthority("ROLE_${user.role.name.uppercase()}")

        return org.springframework.security.core.userdetails.User(
            user.email,
            user.passwordHash,
            listOf(authority)
        )
    }
}