package org.duocuc.capstone_backend.controller

import org.bson.types.ObjectId
import org.duocuc.capstone_backend.repository.DocumentoRepository
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.nio.file.Files
import java.nio.file.Paths
import java.util.UUID
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Criteria

@RestController
class DocumentoDownloadController(
    private val documentoRepo: DocumentoRepository,
    private val gridFsTemplate: GridFsTemplate
) {
    @GetMapping("/api/documentos/{id}/download")
    fun download(@PathVariable id: UUID): ResponseEntity<Resource> {
        val doc = documentoRepo.findById(id).orElseThrow { NoSuchElementException("Documento no encontrado") }

        val filename = doc.fileName
        val contentType = doc.mimeType

        val storage = doc.storagePath
        return when {
            storage.startsWith("gridfs:") -> {
                val oid = storage.removePrefix("gridfs:")
                val file = gridFsTemplate.findOne(
                    Query.query(Criteria.where("_id").`is`(ObjectId(oid)))
                ) ?: throw NoSuchElementException("Archivo no encontrado en GridFS")

                val resource = gridFsTemplate.getResource(file)
                val len = resource.contentLength()

                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$filename\"")
                    .contentLength(len)
                    .body(InputStreamResource(resource.inputStream))            }
            storage.startsWith("file:") -> {
                val p = Paths.get(storage.removePrefix("file:"))
                val isr = InputStreamResource(Files.newInputStream(p))
                val len = Files.size(p)

                ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$filename\"")
                    .contentLength(len)
                    .body(isr)
            }
            else -> {
                throw IllegalStateException("storagePath con esquema desconocido: $storage")
            }
        }
    }
}
