package org.duocuc.capstone_backend.storage

import org.bson.types.ObjectId
import org.springframework.core.io.InputStreamResource
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Component
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

@Component
class StorageStreamOpener(
    private val gridFsTemplate: GridFsTemplate
) {
    fun open(storagePath: String): InputStream {
        return when {
            storagePath.startsWith("gridfs:") -> {
                val oid = ObjectId(storagePath.removePrefix("gridfs:"))
                val file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").`is`(oid)))
                    ?: throw IllegalStateException("Archivo no encontrado en GridFS: $oid")
                val res = gridFsTemplate.getResource(file)
                InputStreamResource(res.inputStream).inputStream
            }
            storagePath.startsWith("file:") -> {
                val p = Paths.get(storagePath.removePrefix("file:"))
                Files.newInputStream(p)
            }
            else -> error("Esquema de storagePath desconocido: $storagePath")
        }
    }
}
