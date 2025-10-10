package org.duocuc.capstonebackend.config

import org.duocuc.capstonebackend.model.Role
import org.duocuc.capstonebackend.model.User
import org.duocuc.capstonebackend.repository.RoleRepository
import org.duocuc.capstonebackend.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class DataInitializer(
    private val roleRepository: RoleRepository,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    private val log = LoggerFactory.getLogger(DataInitializer::class.java)

    @EventListener(ApplicationReadyEvent::class)
    fun onReady() {
        val defaultRoles = listOf("admin", "profesor", "alumno")
        val rolesMap = mutableMapOf<String, Role>()
        defaultRoles.forEach { roleName ->
            val existingRole = roleRepository.findByName(roleName)
            if (existingRole == null) {
                val saved = roleRepository.save(Role(name = roleName))
                rolesMap[roleName] = saved
                log.info("Rol creado por inicializador: ${saved.name} (id=${saved.id})")
            } else {
                rolesMap[roleName] = existingRole
            }
        }

        val adminEmail = "admin@brainboost.cl"
        if (userRepository.findByEmail(adminEmail).isEmpty) {
            val adminRole = rolesMap["admin"] ?: throw IllegalStateException("Rol 'admin' no encontrado.")
            val adminUser = User(
                firstName = "Admin",
                lastName = "User",
                email = adminEmail,
                passwordHash = passwordEncoder.encode("admin1234"),
                role = adminRole,
                state = true,
                createdAt = LocalDateTime.now()
            )
            userRepository.save(adminUser)
            log.info("Usuario administrador por defecto creado con email: $adminEmail")
        }
    }
}


