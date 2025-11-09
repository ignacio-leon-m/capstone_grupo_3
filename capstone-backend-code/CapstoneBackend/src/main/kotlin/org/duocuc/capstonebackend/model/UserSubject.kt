package org.duocuc.capstonebackend.model

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.time.LocalDateTime
import java.util.UUID

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
    val assignDate: LocalDateTime = LocalDateTime.now(),

    @Column(name = "activo", nullable = false)
    var active: Boolean = true
) {
    override fun toString(): String {
        return "UserSubject(id=$id, userId=${user.id}, subjectId=${subject.id}, active=$active)"
    }
}
