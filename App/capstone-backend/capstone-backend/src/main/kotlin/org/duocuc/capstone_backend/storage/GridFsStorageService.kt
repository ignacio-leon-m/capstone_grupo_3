package org.duocuc.capstone_backend.storage

import org.bson.Document
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.gridfs.GridFsResource
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class GridFsStorageService(
    private val gridFsTemplate: GridFsTemplate
) {

    /**
     * Sube un archivo a GridFS y retorna el ObjectId en String.
     * Puedes pasar metadatos adicionales (idTema, idMateria, usuario, etc.).
     */
    fun save(file: MultipartFile, extraMeta: Map<String, Any?> = emptyMap()): String {
        val meta = Document().apply {
            put("filename", file.originalFilename)
            put("contentType", file.contentType)
            put("size", file.size)
            extraMeta.forEach { (k, v) -> if (v != null) put(k, v) }
        }
        val id = gridFsTemplate.store(
            file.inputStream,
            file.originalFilename,
            file.contentType ?: "application/octet-stream",
            meta
        )
        return id.toString()
    }

    /**
     * Obtiene un recurso descargable por ObjectId de GridFS.
     */
    fun open(objectId: String): GridFsResource? {
        val file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").`is`(ObjectId(objectId))))
        return file?.let { gridFsTemplate.getResource(it) }
    }

    /**
     * Elimina un archivo por ObjectId.
     */
    fun delete(objectId: String) {
        gridFsTemplate.delete(Query.query(Criteria.where("_id").`is`(ObjectId(objectId))))
    }
}
