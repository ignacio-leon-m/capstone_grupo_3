package org.duocuc.capstonebackend.dto

import java.util.UUID

/**
 * DTO to start a new game.
 */
data class GameStartDto(
    val userId: UUID,
    val subjectId: UUID,
    val gameName: String
)

/**
 * DTO for game in progress response.
 */
data class GameResponseDto(
    val id: UUID,
    val gameName: String,
    val gameStatus: String,
    val attemptsRemaining: Int?,
    val score: String?,
    val startDate: String,
    val endDate: String?
)

/**
 * DTO to end a game.
 */
data class GameEndDto(
    val gameId: UUID,
    val finalScore: String
)
