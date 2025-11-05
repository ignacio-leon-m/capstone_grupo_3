package org.duocuc.capstonebackend.model

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.time.LocalDateTime
import java.util.UUID

/**
 * Métricas granulares del juego Hangman - cada intento de letra.
 * Patrón escalable: metricas_juego_{nombre}
 */
@Entity
@Table(name = "metricas_juego_hangman", schema = "public")
class HangmanMetric(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_juego", nullable = false)
    var game: Game,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_concepto", nullable = false)
    var concept: Concept,

    @Column(name = "letra_intentada", nullable = false, length = 1)
    var attemptedLetter: Char,

    @Column(name = "es_correcta", nullable = false)
    var isCorrect: Boolean,

    @Column(name = "posicion_letra")
    var letterPosition: Int? = null,

    @Column(name = "tiempo_respuesta_ms")
    var responseTimeMs: Int? = null,

    @Column(name = "fecha_hora")
    var timestamp: LocalDateTime = LocalDateTime.now()
) {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    var id: UUID? = null
}
