package org.duocuc.capstonebackend.dto

import java.util.UUID

/**
 * DTO para iniciar un nuevo juego.
 */
data class GameStartDto(
    val idUsuario: UUID,
    val idAsignatura: UUID,
    val nombreJuego: String
)

/**
 * DTO para respuesta de juego en curso.
 */
data class GameResponseDto(
    val id: UUID,
    val nombreJuego: String,
    val estadoPartida: String,
    val intentosRestantes: Int?,
    val puntaje: String?,
    val fechaInicio: String,
    val fechaFin: String?
)

/**
 * DTO para finalizar un juego.
 */
data class GameEndDto(
    val idJuego: UUID,
    val puntajeFinal: String
)
