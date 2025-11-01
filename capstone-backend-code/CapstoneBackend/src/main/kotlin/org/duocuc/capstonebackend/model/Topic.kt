package org.duocuc.capstonebackend.model

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.UUID

// Topic.kt
@Entity
@Table(
    name = "temas",
    schema = "public",
    uniqueConstraints = [UniqueConstraint(
        name = "uk_tema_nombre_asignatura",
        columnNames = ["nombre","id_asignatura"]
    )]
)
class Topic(
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID? = null,

    @Column(name = "nombre", nullable = false, length = 100)
    var name: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_asignatura", nullable = false)
    var subject: Subject
)
