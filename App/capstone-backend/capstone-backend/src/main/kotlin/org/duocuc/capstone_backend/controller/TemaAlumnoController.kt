package org.duocuc.capstone_backend.controller

import org.duocuc.capstone_backend.dto.TemaDetailDto
import org.duocuc.capstone_backend.dto.TemaListItemDto
import org.duocuc.capstone_backend.service.TemaAlumnoService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api")
class TemaAlumnoController(
    private val temaAlumnoService: TemaAlumnoService
) {
    @GetMapping("/materias/{materiaId}/temas")
    fun listar(@PathVariable materiaId: UUID): ResponseEntity<List<TemaListItemDto>> =
        ResponseEntity.ok(temaAlumnoService.listarPorMateria(materiaId))

    @GetMapping("/temas/{temaId}")
    fun detalle(@PathVariable temaId: UUID): ResponseEntity<TemaDetailDto> =
        ResponseEntity.ok(temaAlumnoService.detalle(temaId))
}
