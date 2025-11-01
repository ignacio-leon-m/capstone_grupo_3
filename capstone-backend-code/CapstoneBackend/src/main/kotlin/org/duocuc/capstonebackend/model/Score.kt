package org.duocuc.capstonebackend.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.UuidGenerator
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "puntajes")
class Score (
    @Id
    @UuidGenerator
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val id: UUID? = null,

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    val user: User,

    @ManyToOne
    @JoinColumn(name = "id_asignatura", nullable = false)
    val subject: Subject,

    @Column(name = "puntaje", nullable = false, precision = 10, scale = 2)
    val score: BigDecimal,

    @Column(name = "fecha_asignacion", nullable = false)
    val assignmentDate: LocalDate = LocalDate.now()
)