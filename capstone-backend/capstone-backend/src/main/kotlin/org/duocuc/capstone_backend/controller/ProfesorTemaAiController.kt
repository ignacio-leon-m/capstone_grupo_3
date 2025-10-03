package org.duocuc.capstone_backend.controller

import org.duocuc.capstone_backend.ai.GeneracionAiResultado
import org.duocuc.capstone_backend.ai.TemaAiService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/profesores/temas")
class ProfesorTemaAiController(
    private val aiService: TemaAiService
) {
    @PostMapping("/{temaId}/ai")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESOR')")
    fun generar(@PathVariable temaId: UUID): ResponseEntity<GeneracionAiResultado> {
        val out = aiService.generarResumenYQuiz(temaId)
        return ResponseEntity.ok(out)
    }
}
