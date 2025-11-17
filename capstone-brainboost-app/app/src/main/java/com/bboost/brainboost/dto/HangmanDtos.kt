package com.bboost.brainboost.dto

import java.util.UUID

// ======== HANGMAN DTOS (todo en un solo archivo) ========

data class HangmanGameStartDto(
    val userId: UUID,
    val subjectId: UUID,
    val topicId: UUID
)

data class HangmanConceptDto(
    val id: UUID,
    val word: String,   // llega completa, se enmascara en el front
    val hint: String?
)

data class HangmanGameStateDto(
    val gameId: UUID,
    val userId: UUID,
    val subjectId: UUID,
    val concepts: List<HangmanConceptDto>,
    val currentConceptIndex: Int,
    val livesRemaining: Int,
    val gameStatus: String, // "activo", etc.
    val score: Int
)

data class HangmanAttemptDto(
    val conceptId: UUID,
    val letter: String,
    val responseTimeMs: Long? = null
)

data class HangmanAttemptResponseDto(
    val isCorrect: Boolean,
    val positions: List<Int>,
    val livesRemaining: Int,
    val gameOver: Boolean
)

data class HangmanConceptSubmitDto(
    val conceptId: UUID,
    val guessed: Boolean,
    val timeMs: Long
)

data class HangmanConceptResultDto(
    val conceptId: UUID,
    val word: String,
    val guessed: Boolean,
    val attemptsUsed: Int,
    val totalTimeMs: Long,
    val scoreObtained: Int,
    val livesRemaining: Int
)

data class HangmanGameResultDto(
    val gameId: UUID,
    val finalScore: Int,
    val conceptsCompleted: Int,
    val totalConcepts: Int,
    val livesRemaining: Int,
    val conceptResults: List<HangmanConceptResultDto>
)
