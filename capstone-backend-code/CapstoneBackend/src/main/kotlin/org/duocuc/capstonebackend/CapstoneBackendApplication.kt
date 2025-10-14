package org.duocuc.capstonebackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories("org.duocuc.capstonebackend.repository")
class CapstoneBackendApplication

fun main(args: Array<String>) {
	runApplication<CapstoneBackendApplication>(*args)
}
