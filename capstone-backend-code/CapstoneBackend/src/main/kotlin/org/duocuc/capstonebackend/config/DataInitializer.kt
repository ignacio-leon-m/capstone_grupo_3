package org.duocuc.capstonebackend.config

import org.duocuc.capstonebackend.model.Role
import org.duocuc.capstonebackend.repository.RoleRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class DataInitializer(
    private val roleRepository: RoleRepository
) {
    private val log = LoggerFactory.getLogger(DataInitializer::class.java)

    @EventListener(ApplicationReadyEvent::class)
    fun onReady() {
        val defaultRoles = listOf("admin", "profesor", "alumno")
        defaultRoles.forEach { roleName ->
            val existing = roleRepository.findByName(roleName)
            if (existing == null) {
                val saved = roleRepository.save(Role(name = roleName))
                log.info("Rol creado por inicializador: ${'$'}{saved.name} (id=${'$'}{saved.id})")
            } else {
                log.info("Rol existente: ${'$'}{existing.name} (id=${'$'}{existing.id})")
            }
        }
    }
}


