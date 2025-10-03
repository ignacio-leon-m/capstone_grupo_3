package org.duocuc.capstone_backend.model

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.time.LocalDateTime
import java.util.UUID
import com.fasterxml.jackson.databind.JsonNode
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Entity
@Table(name = "temas")
class Tema(
    @Id
    @UuidGenerator
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val id: UUID? = null,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_materia", nullable = false)
    val materia: Materia,

    @Column(name = "titulo", nullable = false, length = 150)
    val titulo: String,

    @Column(name = "descripcion")
    val descripcion: String? = null,

    @Column(name = "resumen", columnDefinition = "text")
    var resumen: String? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "quiz", columnDefinition = "jsonb")
    var quiz: JsonNode? = null,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por", nullable = false)
    val creadoPor: User,

    @Column(name = "creado_en", nullable = false)
    var creadoEn: LocalDateTime = LocalDateTime.now(),

    @Column(name = "actualizado_en", nullable = false)
    var actualizadoEn: LocalDateTime = LocalDateTime.now(),

    // --- AGREGAR ESTA LÍNEA ---
    @OneToMany(mappedBy = "tema", cascade = [CascadeType.ALL], orphanRemoval = true)
    val documentos: MutableList<Documento> = mutableListOf()
) {
    @PreUpdate
    fun touch() {
        actualizadoEn = LocalDateTime.now()
    }
}