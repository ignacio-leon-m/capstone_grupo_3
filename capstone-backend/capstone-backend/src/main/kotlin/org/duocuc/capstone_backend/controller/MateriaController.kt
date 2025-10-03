package org.duocuc.capstone_backend.controller

import org.duocuc.capstone_backend.dto.MateriaDto
import org.duocuc.capstone_backend.service.MateriaService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/materias")
class MateriaController(
    private val materiaService: MateriaService
) {
    @GetMapping
    fun list(): ResponseEntity<List<MateriaDto>> =
        ResponseEntity.ok(materiaService.listAll())
}
