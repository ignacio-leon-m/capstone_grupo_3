package org.duocuc.capstone_backend.controller

import org.duocuc.capstone_backend.dto.TemaResponseDto
import org.duocuc.capstone_backend.service.TemaProfesorService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("/api/profesores")
class ProfesorTemaController(
    private val temaProfesorService: TemaProfesorService
) {

    @PostMapping(
        "/materias/{materiaId}/temas",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    @PreAuthorize("hasAnyRole('PROFESOR','ADMIN')")
    fun crearTemaConDocumento(
        @PathVariable materiaId: UUID,
        @RequestPart("titulo") titulo: String,
        @RequestPart(value = "descripcion", required = false) descripcion: String?,
        @RequestPart("file") file: MultipartFile
    ): ResponseEntity<TemaResponseDto> {
        val email = SecurityContextHolder.getContext().authentication.name
        val dto = temaProfesorService.crearTemaConDocumento(
            materiaId = materiaId,
            titulo = titulo,
            descripcion = descripcion,
            archivo = file,
            creadorEmail = email
        )
        return ResponseEntity(dto, HttpStatus.CREATED)
    }

    // Endpoints para eliminar un documento específico
    @PreAuthorize("hasAnyRole('ADMIN','PROFESOR')")
    @DeleteMapping("/temas/{temaId}/documentos/{docId}")
    fun eliminarDocumentoPorTema(
        @PathVariable temaId: UUID,
        @PathVariable docId: String
    ): ResponseEntity<Void> = eliminarDocumentoInterno(temaId, docId)

    @PreAuthorize("hasAnyRole('ADMIN','PROFESOR')")
    @DeleteMapping("/materias/{materiaId}/temas/{temaId}/documentos/{docId}")
    fun eliminarDocumentoPorMateriaYTema(
        @PathVariable materiaId: UUID,
        @PathVariable temaId: UUID,
        @PathVariable docId: String
    ): ResponseEntity<Void> = eliminarDocumentoInterno(temaId, docId)

    // Función privada para manejar la lógica de eliminación de documentos
    private fun eliminarDocumentoInterno(temaId: UUID, docId: String): ResponseEntity<Void> {
        temaProfesorService.eliminarDocumento(temaId, docId)
        return ResponseEntity.noContent().build()
    }

    // Endpoints para borrar solo el contenido de AI (resumen + quiz)
    @PreAuthorize("hasAnyRole('ADMIN','PROFESOR')")
    @DeleteMapping("/temas/{temaId}/ai")
    fun borrarAi(@PathVariable temaId: UUID): ResponseEntity<Void> {
        temaProfesorService.eliminarSoloAi(temaId)
        return ResponseEntity.noContent().build()
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESOR')")
    @DeleteMapping("/materias/{materiaId}/temas/{temaId}/ai")
    fun borrarAiConMateria(
        @PathVariable materiaId: UUID,
        @PathVariable temaId: UUID
    ): ResponseEntity<Void> = borrarAi(temaId)

    // Endpoints para borrar un tema completo (incluidos documentos y IA)
    @PreAuthorize("hasAnyRole('ADMIN','PROFESOR')")
    @DeleteMapping("/temas/{temaId}")
    fun borrarTema(@PathVariable temaId: UUID): ResponseEntity<Void> {
        temaProfesorService.eliminarTema(temaId)
        return ResponseEntity.noContent().build()
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESOR')")
    @DeleteMapping("/materias/{materiaId}/temas/{temaId}")
    fun borrarTemaConMateria(
        @PathVariable materiaId: UUID,
        @PathVariable temaId: UUID
    ): ResponseEntity<Void> = borrarTema(temaId)
}