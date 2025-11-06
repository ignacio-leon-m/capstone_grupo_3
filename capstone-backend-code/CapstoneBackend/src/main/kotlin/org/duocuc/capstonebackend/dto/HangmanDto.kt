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
    val currentConcept: HangmanConceptDto,
    val livesRemaining: Int,
    val currentScore: Int,
    val conceptNumber: Int,
    val totalConcepts: Int,
    val usedLetters: List<Char>,
    val wordProgress: String  // Ex: "_ _ C _ R S I _ _ "
)

/**
 * DTO for a concept in Hangman.
 */
data class HangmanConceptDto(
    val conceptId: UUID,
    val hiddenWord: String,  // Only for backend, don't send to frontend
    val hint: String,
    val length: Int
)

/**
 * DTO to attempt a letter in Hangman.
 */
data class HangmanAttemptDto(
    val gameId: UUID,
    val conceptId: UUID,
    val letter: Char
)

/**
 * DTO for response to an attempt in Hangman.
 */
data class HangmanAttemptResponseDto(
    val isCorrect: Boolean,
    val message: String,
    val wordProgress: String,
    val livesRemaining: Int,
    val conceptCompleted: Boolean,
    val gameOver: Boolean,
    val finalScore: Int?
)

/**
 * DTO for final result of a Hangman game.
 */
data class HangmanGameResultDto(
    val gameId: UUID,
    val conceptsCompleted: Int,
    val totalConcepts: Int,
    val finalScore: Int,
    val livesUsed: Int,
    val totalTimeMs: Int?,
    val resultsByConcept: List<HangmanConceptResultDto>
)

/**
 * DTO for individual concept result in Hangman.
 */
data class HangmanConceptResultDto(
    val word: String,
    val guessed: Boolean,
    val attemptsUsed: Int,
    val timeMs: Int?
)
