package org.duocuc.capstone_backend.service

import com.fasterxml.jackson.databind.JsonNode
import org.duocuc.capstone_backend.ai.GeminiService
import org.duocuc.capstone_backend.ai.TextExtractor
import org.duocuc.capstone_backend.dto.DocumentoDto
import org.duocuc.capstone_backend.dto.TemaResponseDto
import org.duocuc.capstone_backend.model.Documento
import org.duocuc.capstone_backend.model.Tema
import org.duocuc.capstone_backend.repository.*
import org.duocuc.capstone_backend.storage.GridFsStorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.UUID
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Criteria

@Service
class TemaProfesorService(
    private val materiaRepo: MateriaRepository,
    private val userRepo: UserRepository,
    private val temaRepo: TemaRepository,
    private val documentoRepo: DocumentoRepository,
    private val storage: GridFsStorageService,
    private val textExtractor: TextExtractor,
    private val gemini: GeminiService,
    private val temaRepository: TemaRepository,
    private val gridFsTemplate: GridFsTemplate
) {

    @Transactional
    fun crearTemaConDocumento(
        materiaId: UUID,
        titulo: String,
        descripcion: String?,
        archivo: MultipartFile,
        creadorEmail: String
    ): TemaResponseDto {
        val materia = materiaRepo.findById(materiaId)
            .orElseThrow { IllegalArgumentException("Materia no encontrada: $materiaId") }

        val user = userRepo.findByEmail(creadorEmail)
            .orElseThrow { IllegalArgumentException("Usuario no encontrado: $creadorEmail") }

        val objectId = storage.save(
            archivo,
            mapOf("materiaId" to materiaId.toString(), "creadoPor" to creadorEmail)
        )

        val tema = temaRepo.save(
            Tema(
                materia = materia,
                titulo = titulo,
                descripcion = descripcion,
                creadoPor = user
            )
        )

        val doc = documentoRepo.save(
            Documento(
                tema = tema,
                fileName = archivo.originalFilename ?: "documento",
                mimeType = archivo.contentType ?: "application/octet-stream",
                storagePath = "gridfs:$objectId",
                sizeBytes = archivo.size
            )
        )

        try {
            val texto = textExtractor.extract(doc.storagePath)
            val resumen = gemini.resumen(texto)
            val quizJson: JsonNode = gemini.quiz(texto)

            tema.resumen = resumen
            tema.quiz = quizJson
            temaRepo.save(tema)
        } catch (e: Exception) {
            println("WARN: No se pudo generar el resumen/quiz para el documento ${doc.fileName}: ${e.message}")
        }

        return TemaResponseDto.from(tema, listOf(doc))
    }

    @Transactional
    fun eliminarDocumento(temaId: UUID, docId: String) {
        val tema = temaRepository.findById(temaId)
            .orElseThrow { IllegalArgumentException("Tema no encontrado") }

        val existe = tema.documentos.any { it.id?.toString() == docId }
        if (!existe) throw IllegalArgumentException("Documento no está asociado al tema")

        gridFsTemplate.delete(
            Query.query(
                Criteria.where("metadata.temaId").`is`(temaId.toString())
                    .and("metadata.docId").`is`(docId)
            )
        )

        val nuevaLista = tema.documentos.filterNot { it.id?.toString() == docId }
        tema.documentos.clear()
        tema.documentos.addAll(nuevaLista)

        temaRepository.save(tema)
    }

    @Transactional
    fun eliminarSoloAi(temaId: UUID) {
        val tema = temaRepository.findById(temaId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Tema no encontrado") }

        tema.resumen = null
        tema.quiz = null

        temaRepository.save(tema)
    }

    @Transactional
    fun eliminarTema(temaId: UUID) {
        gridFsTemplate.delete(
            Query.query(Criteria.where("metadata.temaId").`is`(temaId.toString()))
        )

        if (temaRepository.existsById(temaId)) {
            temaRepository.deleteById(temaId)
        } else {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Tema no encontrado")
        }
    }
}