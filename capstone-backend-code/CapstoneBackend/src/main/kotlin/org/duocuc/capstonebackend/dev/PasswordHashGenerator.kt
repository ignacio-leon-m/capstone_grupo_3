package org.duocuc.capstonebackend.dev

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordHashGenerator(
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(PasswordHashGenerator::class.java)

    override fun run(vararg args: String?) {
        val plainPassword = "duoc123"
        val hashedPassword = passwordEncoder.encode(plainPassword)
        log.info("GENERADOR DE HASH DE CONTRASEÑA")
        log.info("Contraseña en texto plano: {}", plainPassword)
        log.info("Hash (BCrypt) generado: {}", hashedPassword)
        log.info("Copiar hash y usarlo en script.sql")
    }
}