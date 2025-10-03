package org.duocuc.capstone_backend.controller

import com.fasterxml.jackson.databind.JsonNode
import org.duocuc.capstone_backend.repository.TemaRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api")
class TemaQuizController(
    private val temaRepo: TemaRepository
) {
    @GetMapping("/temas/{temaId}/quiz", produces = ["application/json"])
    fun getQuiz(@PathVariable temaId: UUID): ResponseEntity<JsonNode> {
        val tema = temaRepo.findById(temaId).orElseThrow()
        val quiz = tema.quiz ?: return ResponseEntity.noContent().build()
        return ResponseEntity.ok(quiz)
    }
}
