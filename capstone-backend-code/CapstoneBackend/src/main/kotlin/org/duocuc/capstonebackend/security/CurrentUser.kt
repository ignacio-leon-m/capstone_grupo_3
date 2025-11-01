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
        val email = email()
        val user = userRepository.findByEmail(email)
            .orElseThrow { IllegalStateException("Usuario autenticado no encontrado en la base de datos: $email") }
        return user.id ?: throw IllegalStateException("El ID del usuario no puede ser nulo.")
    }

    fun email(): String {
        val auth = SecurityContextHolder.getContext().authentication
            ?: throw IllegalStateException("No hay autenticación en SecurityContext")
        return auth.name
    }
}