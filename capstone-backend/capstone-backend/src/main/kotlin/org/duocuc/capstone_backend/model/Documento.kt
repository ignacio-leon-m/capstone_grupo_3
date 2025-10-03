package org.duocuc.capstone_backend.model

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "documentos")
class Documento(
    @Id
    @UuidGenerator
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val id: UUID? = null,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tema", nullable = false)
    val tema: Tema,

    @Column(name = "file_name", nullable = false, length = 255)
    val fileName: String,

    @Column(name = "mime_type", nullable = false, length = 120)
    val mimeType: String,

    @Column(name = "storage_path", nullable = false, length = 500)
    val storagePath: String,          // ej: "gridfs:<objectId>"

    @Column(name = "size_bytes", nullable = false)
    val sizeBytes: Long,

    @Column(name = "subido_en", nullable = false)
    val subidoEn: LocalDateTime = LocalDateTime.now()
)
