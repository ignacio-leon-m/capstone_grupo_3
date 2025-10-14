package org.duocuc.capstonebackend.config

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

/**
 * Componente temporal para generar un hash de contraseña. 
 * Se ejecuta una vez al iniciar la aplicación y muestra el hash en la consola.
 * Una vez obtenido el hash, se debe desactivar comentando la anotación @Component.
 */
@Component
class PasswordHashGenerator(
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(PasswordHashGenerator::class.java)

    override fun run(vararg args: String?) {
        val plainPassword = "duoc123" // La contraseña que quieres hashear
        val hashedPassword = passwordEncoder.encode(plainPassword)

        log.info("--- GENERADOR DE HASH DE CONTRASEÑA ---")
        log.info("Contraseña en texto plano: {}", plainPassword)
        log.info("Hash (BCrypt) generado: {}", hashedPassword)
        log.info("--- COPIA EL HASH Y PÉGALO EN TU SCRIPT.SQL ---")
    }
}