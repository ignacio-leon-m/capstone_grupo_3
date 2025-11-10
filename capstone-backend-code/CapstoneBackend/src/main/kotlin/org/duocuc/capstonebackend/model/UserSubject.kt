package org.duocuc.capstonebackend.model

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.time.LocalDateTime
import java.util.UUID

/**
 * Entidad que representa la relación N:M entre usuarios y asignaturas.
 * El rol del usuario determina si es una asignación de profesor o inscripción de alumno.
 */
@Entity
@Table(
    name = "usuario_asignatura",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["id_usuario", "id_asignatura"])
    ]
)
class UserSubject(
    @Id
    @UuidGenerator
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_asignatura", nullable = false)
    val subject: Subject,

    @Column(name = "fecha_asignacion", nullable = false)
    val enrollmentDate: LocalDateTime = LocalDateTime.now(),

    @Column(name = "activo", nullable = false)
    var active: Boolean = true
) {
}
