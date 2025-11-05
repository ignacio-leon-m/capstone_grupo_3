package org.duocuc.capstonebackend.model

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.time.LocalDateTime
import java.util.UUID

/**
 * Concepto académico extraído automáticamente por IA (Gemini) desde PDFs.
 * Tabla simplificada - todos los conceptos provienen de extracción automatizada.
 */
@Entity
@Table(name = "conceptos", schema = "public")
class Concept(
    @Column(name = "palabra_concepto", nullable = false, length = 255)
    var word: String,

    @Column(name = "hint", columnDefinition = "text")
    var hint: String? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tema", nullable = false)
    var topic: Topic,

    @Column(name = "fecha_creacion")
    var createdAt: LocalDateTime = LocalDateTime.now()
) {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    var id: UUID? = null
}
