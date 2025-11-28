package org.duocuc.capstonebackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories("org.duocuc.capstonebackend.repository")
class CapstoneBackendApplication

fun main(args: Array<String>) {
	val ctx = runApplication<CapstoneBackendApplication>(*args)
	val port = ctx.environment.getProperty("server.port") ?: "8080"
	println("\n========================================")
	println("Servidor iniciado correctamente en: http://localhost:$port/")
	println("Presiona Ctrl+C para detener el servidor.")
	println("========================================\n")
}
