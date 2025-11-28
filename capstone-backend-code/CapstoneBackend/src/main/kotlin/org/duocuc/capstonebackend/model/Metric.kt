package org.duocuc.capstonebackend.model

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "metricas", schema = "public")
class Metric(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pregunta", nullable = false)
    var question: Question,

    @Column(name = "respuesta_correcta", nullable = false)
    var correctAnswer: Boolean,

    @Column(name = "tiempo_respuesta_ms")
    var responseTimeMs: Int? = null,

    @Column(name = "fecha_hora", nullable = false)
    var timestamp: LocalDateTime = LocalDateTime.now()
) {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    var id: UUID? = null
}
