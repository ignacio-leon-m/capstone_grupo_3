package org.duocuc.capstonebackend.model

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.UUID

@Entity
@Table(name = "preguntas", schema = "public")
class Question(
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID? = null,

    @Column(name = "texto", nullable = false, columnDefinition = "text")
    var text: String,

    @Column(name = "respuesta_correcta", nullable = false, columnDefinition = "text")
    var correctAnswer: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_asignatura", nullable = false)
    var subject: Subject,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tema", nullable = false)
    var topic: Topic
)