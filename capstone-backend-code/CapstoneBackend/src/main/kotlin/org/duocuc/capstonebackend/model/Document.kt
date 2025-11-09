package org.duocuc.capstonebackend.model

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.time.LocalDateTime
import java.util.UUID

/**
 * Document uploaded by professors (PDF, DOCX, TXT).
 * Represents the raw content file that will be processed by Content Service.
 * 
 * Aligns with the project report:
 * - Professors upload theoretical content (documents)
 * - These are stored for IA Service to analyze
 * - Each document can generate multiple topics
 */
@Entity
@Table(name = "documentos", schema = "public")
class Document(
    @Column(name = "nombre_archivo", nullable = false, length = 255)
    var fileName: String,

    @Column(name = "ruta_archivo", nullable = false, length = 500)
    var filePath: String,

    @Column(name = "tipo_documento", nullable = false, length = 50)
    var documentType: String, // PDF, DOCX, TXT

    @Column(name = "tamano_bytes", nullable = false)
    var sizeBytes: Long,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_asignatura", nullable = false)
    var subject: Subject,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario_carga", nullable = false)
    var uploadedBy: User,

    @Column(name = "fecha_carga")
    var uploadedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "procesado", nullable = false)
    var processed: Boolean = false,

    @Column(name = "fecha_procesamiento")
    var processedAt: LocalDateTime? = null,

    @Column(name = "numero_conceptos_extraidos")
    var conceptsExtracted: Int? = null,

    @Column(name = "texto_extraido", columnDefinition = "text")
    var extractedText: String? = null
) {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    var id: UUID? = null
}
