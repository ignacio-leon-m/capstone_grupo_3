package org.duocuc.capstonebackend.dto

import java.util.UUID

/**
 * DTO para iniciar una partida de Hangman.
 */
data class HangmanGameStartDto(
    val idUsuario: UUID,
    val idAsignatura: UUID,
    val idTema: UUID,
    val cantidadConceptos: Int = 10  // 10-12 conceptos por partida
)

/**
 * DTO para el estado actual de una partida de Hangman.
 */
data class HangmanGameStateDto(
    val idJuego: UUID,
    val conceptoActual: HangmanConceptDto,
    val vidasRestantes: Int,
    val puntajeActual: Int,
    val conceptoNumero: Int,
    val totalConceptos: Int,
    val letrasUsadas: List<Char>,
    val palabraProgreso: String  // Ej: "_ _ C _ R S I _ _ "
)

/**
 * DTO para un concepto en Hangman.
 */
data class HangmanConceptDto(
    val idConcepto: UUID,
    val palabraOculta: String,  // Solo para backend, no enviar al frontend
    val hint: String,
    val longitud: Int
)

/**
 * DTO para intentar una letra en Hangman.
 */
data class HangmanAttemptDto(
    val idJuego: UUID,
    val idConcepto: UUID,
    val letra: Char
)

/**
 * DTO para respuesta de un intento en Hangman.
 */
data class HangmanAttemptResponseDto(
    val esCorrecta: Boolean,
    val mensaje: String,
    val palabraProgreso: String,
    val vidasRestantes: Int,
    val conceptoCompletado: Boolean,
    val juegoTerminado: Boolean,
    val puntajeFinal: Int?
)

/**
 * DTO para el resultado final de una partida de Hangman.
 */
data class HangmanGameResultDto(
    val idJuego: UUID,
    val conceptosCompletados: Int,
    val totalConceptos: Int,
    val puntajeFinal: Int,
    val vidasUsadas: Int,
    val tiempoTotalMs: Int?,
    val resultadosPorConcepto: List<HangmanConceptResultDto>
)

/**
 * DTO para el resultado de un concepto individual en Hangman.
 */
data class HangmanConceptResultDto(
    val palabraConcepto: String,
    val adivinado: Boolean,
    val intentosUsados: Int,
    val tiempoMs: Int?
)
