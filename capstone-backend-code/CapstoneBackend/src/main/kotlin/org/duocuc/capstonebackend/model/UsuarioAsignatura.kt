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
class UsuarioAsignatura(
    @Id
    @UuidGenerator
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    val usuario: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_asignatura", nullable = false)
    val asignatura: Subject,

    @Column(name = "fecha_asignacion", nullable = false)
    val fechaAsignacion: LocalDateTime = LocalDateTime.now(),

    @Column(name = "activo", nullable = false)
    var activo: Boolean = true
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UsuarioAsignatura) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String {
        return "UsuarioAsignatura(id=$id, usuarioId=${usuario.id}, asignaturaId=${asignatura.id}, activo=$activo)"
    }
}
