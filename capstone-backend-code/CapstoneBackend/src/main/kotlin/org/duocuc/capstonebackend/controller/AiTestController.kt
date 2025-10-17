// src/main/kotlin/org/duocuc/capstonebackend/controller/AiTestController.kt
package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.dto.AiPromptDto
import org.duocuc.capstonebackend.service.impl.AiService // Asegúrate de que esta interfaz se use
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/ia/test")
class AiTestController(private val ai: AiService) {

    @PostMapping("/prompt")
    @PreAuthorize("hasAnyAuthority('admin','PROFESOR')")
    fun prompt(@RequestBody body: AiPromptDto): ResponseEntity<Map<String, String>> = // Declara el tipo de retorno
        ResponseEntity.ok(mapOf("text" to ai.resumen(body.prompt))) // ⭐️ CAMBIO: simplePrompt -> resumen

    // Agregamos el endpoint de Quiz si quieres usarlo de prueba
    @PostMapping("/quiz")
    @PreAuthorize("hasAnyAuthority('admin','PROFESOR')")
    fun quiz(@RequestBody body: AiPromptDto): ResponseEntity<String> {
        val jsonNode = ai.quiz(body.prompt)
        return ResponseEntity.ok(jsonNode.toString())
    }
}