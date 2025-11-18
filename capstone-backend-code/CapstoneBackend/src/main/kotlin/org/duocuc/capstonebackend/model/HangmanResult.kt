package org.duocuc.capstonebackend.model

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    name = "resultados_juego_hangman",
    schema = "public",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_hangman_result_game_concept",
            columnNames = ["id_juego", "id_concepto"]
        )
    ]
)
class HangmanResult(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_juego", nullable = false)
    var game: Game,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_concepto", nullable = false)
    var concept: Concept,

    @Column(name = "adivinado", nullable = false)
    var guessed: Boolean,

    @Column(name = "intentos_usados", nullable = false)
    var attemptsUsed: Int,

    @Column(name = "tiempo_total_ms")
    var totalTimeMs: Int? = null,

    @Column(name = "puntaje_obtenido", precision = 10, scale = 2)
    var scoreObtained: BigDecimal? = null,

    @Column(name = "vidas_restantes")
    var livesRemaining: Int? = null,

    @Column(name = "fecha_hora")
    var timestamp: LocalDateTime = LocalDateTime.now()
) {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    var id: UUID? = null
}
