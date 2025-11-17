package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.dto.ConceptCreateDto
import org.duocuc.capstonebackend.dto.ConceptResponseDto
import org.duocuc.capstonebackend.service.ConceptService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/topics")
class ConceptController(
    private val conceptService: ConceptService
) {
    @GetMapping("/{topicId}/concepts")
    @PreAuthorize("isAuthenticated()")
    fun getConceptsByTopic(@PathVariable topicId: UUID): ResponseEntity<List<ConceptResponseDto>> {
        return ResponseEntity.ok(conceptService.getConceptsByTopic(topicId))
    }

    @PostMapping("/{topicId}/concepts")
    @PreAuthorize("hasAnyAuthority('profesor','admin')")
    fun createConcepts(
        @PathVariable topicId: UUID,
        @RequestBody concepts: List<ConceptCreateDto>
    ): ResponseEntity<List<ConceptResponseDto>> {
        val created = conceptService.createConcepts(topicId, concepts)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }
}
