package org.duocuc.capstonebackend.model

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "juegos", schema = "public")
class Game(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_asignatura", nullable = false)
    var subject: Subject,

    @Column(name = "nombre_juego", length = 50)
    var gameName: String? = null,

    @Column(name = "intentos_restantes")
    var attemptsRemaining: Int? = null,

    @Column(name = "estado_partida", nullable = false, length = 50)
    var gameStatus: String = "En curso",

    @Column(name = "fecha_inicio", nullable = false)
    var startDate: LocalDateTime = LocalDateTime.now(),

    @Column(name = "fecha_fin")
    var endDate: LocalDateTime? = null,

    @Column(name = "puntaje", precision = 10, scale = 2)
    var score: BigDecimal? = null
) {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    var id: UUID? = null
}
