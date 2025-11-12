package org.duocuc.capstonebackend.security

import org.duocuc.capstonebackend.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.*

@Component
class CurrentUser(private val userRepository: UserRepository) {

    /**
     * Devuelve el UUID de usuarios. Lanza IllegalStateException si no encuentra.
     */
    fun id(): UUID {
        val auth = SecurityContextHolder.getContext().authentication
            ?: throw IllegalStateException("No hay autenticación en SecurityContext")

        val email = auth.name ?: throw IllegalStateException("Principal sin email")
        val user = userRepository.findByEmail(email).orElseThrow {
            IllegalStateException("Usuario no encontrado por email: $email")
        }
        return user.id ?: throw IllegalStateException("Usuario sin UUID")
    }

    fun email(): String {
        val auth = SecurityContextHolder.getContext().authentication
            ?: throw IllegalStateException("No hay autenticación en SecurityContext")
        return auth.name
    }

    fun role(): String {
        val auth = SecurityContextHolder.getContext().authentication
            ?: throw IllegalStateException("No hay autenticación en SecurityContext")
        return auth.authorities.firstOrNull()?.authority
            ?: throw IllegalStateException("Usuario sin autoridad")
    }
}
