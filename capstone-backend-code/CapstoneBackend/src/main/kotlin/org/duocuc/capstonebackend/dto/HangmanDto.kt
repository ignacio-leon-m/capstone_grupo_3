package org.duocuc.capstonebackend.dto

import java.util.UUID

/**
 * DTO to start a Hangman game.
 */
data class HangmanGameStartDto(
    val userId: UUID,
    val subjectId: UUID,
    val topicId: UUID,
    val conceptCount: Int = 10  // 10-12 concepts per game
)

/**
 * DTO for current state of a Hangman game.
 */
data class HangmanGameStateDto(
    val gameId: UUID,
    val userId: UUID,
    val subjectId: UUID,
    val concepts: List<HangmanConceptDto>,
    val currentConceptIndex: Int,
    val livesRemaining: Int,
    val gameStatus: String,
    val score: Int
)

/**
 * DTO for a concept in Hangman.
 */
data class HangmanConceptDto(
    val id: UUID,
    val word: String,
    val hint: String?
)

/**
 * DTO to attempt a letter in Hangman.
 */
data class HangmanAttemptDto(
    val conceptId: UUID,
    val letter: String,
    val responseTimeMs: Long? = null
)

/**
 * DTO for response to an attempt in Hangman.
 */
data class HangmanAttemptResponseDto(
    val isCorrect: Boolean,
    val positions: List<Int>,
    val livesRemaining: Int,
    val gameOver: Boolean
)

/**
 * DTO for final result of a Hangman game.
 */
data class HangmanGameResultDto(
    val gameId: UUID,
    val finalScore: Int,
    val conceptsCompleted: Int,
    val totalConcepts: Int,
    val livesRemaining: Int,
    val conceptResults: List<HangmanConceptResultDto>
)

/**
 * DTO for individual concept result in Hangman.
 */
data class HangmanConceptResultDto(
    val conceptId: UUID,
    val word: String,
    val guessed: Boolean,
    val attemptsUsed: Int,
    val totalTimeMs: Long,
    val scoreObtained: Int,
    val livesRemaining: Int
)

/**
 * DTO to submit a concept completion/result.
 */
data class HangmanConceptSubmitDto(
    val conceptId: UUID,
    val guessed: Boolean,
    val timeMs: Long
)
