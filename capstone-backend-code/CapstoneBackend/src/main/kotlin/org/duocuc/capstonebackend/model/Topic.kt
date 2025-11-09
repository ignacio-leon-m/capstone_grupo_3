package org.duocuc.capstonebackend.model

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.UUID

/**
 * Topic (Theme) for organizing academic content.
 * Can be created from:
 * - PDF document analysis (via Content Service)
 * - Manual creation by professors
 * 
 * Each topic contains multiple concepts for games.
 */
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
    @Column(name = "nombre", nullable = false, length = 100)
    var name: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_asignatura", nullable = false)
    var subject: Subject,

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "id_documento", nullable = true)
    var document: Document? = null
) {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    var id: UUID? = null
}
