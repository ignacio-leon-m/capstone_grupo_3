package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.dto.*
import org.duocuc.capstonebackend.service.HangmanService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * REST Controller for Hangman game operations.
 * Provides endpoints to start, play, and complete Hangman games.
 */
@RestController
@RequestMapping("/api/hangman")
class HangmanController(
    private val hangmanService: HangmanService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Start a new Hangman game.
     * POST /api/hangman/start
     * 
     * @param request Contains userId, subjectId, and topicId
     * @return HangmanGameStateDto with initial game state
     */
    @PostMapping("/start")
    fun startGame(@RequestBody request: HangmanGameStartDto): ResponseEntity<HangmanGameStateDto> {
        logger.info("POST /api/hangman/start - Starting new game for user ${request.userId}")
        
        val gameState = hangmanService.startHangmanGame(request)
        
        logger.info("Game started successfully: gameId=${gameState.gameId}, concepts=${gameState.concepts.size}")
        return ResponseEntity.status(HttpStatus.CREATED).body(gameState)
    }

    /**
     * Get current game progress/state.
     * GET /api/hangman/games/{gameId}
     * 
     * @param gameId UUID of the game
     * @return Current game state
     */
    @GetMapping("/games/{gameId}")
    fun getGameProgress(@PathVariable gameId: UUID): ResponseEntity<HangmanGameStateDto> {
        logger.debug("GET /api/hangman/games/$gameId - Fetching game progress")
        
        val gameState = hangmanService.getGameProgress(gameId)
        
        return ResponseEntity.ok(gameState)
    }

    /**
     * Attempt a letter for a specific concept.
     * POST /api/hangman/games/{gameId}/attempt
     * 
     * @param gameId UUID of the game
     * @param request Contains conceptId and letter to attempt
     * @return Result of the letter attempt
     */
    @PostMapping("/games/{gameId}/attempt")
    fun attemptLetter(
        @PathVariable gameId: UUID,
        @RequestBody request: HangmanAttemptDto
    ): ResponseEntity<HangmanAttemptResponseDto> {
        logger.debug("POST /api/hangman/games/$gameId/attempt - Letter: ${request.letter}")
        
        val response = hangmanService.attemptLetter(gameId, request)
        
        return ResponseEntity.ok(response)
    }

    /**
     * Submit the result for a completed concept.
     * POST /api/hangman/games/{gameId}/concepts/submit
     * 
     * @param gameId UUID of the game
     * @param request Contains conceptId, guessed status, and time taken
     * @return Result of the concept
     */
    @PostMapping("/games/{gameId}/concepts/submit")
    fun submitConceptResult(
        @PathVariable gameId: UUID,
        @RequestBody request: HangmanConceptSubmitDto
    ): ResponseEntity<HangmanConceptResultDto> {
        logger.info("POST /api/hangman/games/$gameId/concepts/submit - Concept: ${request.conceptId}")
        
        val result = hangmanService.submitConceptResult(
            gameId = gameId,
            conceptId = request.conceptId,
            guessed = request.guessed,
            timeMs = request.timeMs
        )
        
        return ResponseEntity.ok(result)
    }

    /**
     * End/finalize a Hangman game.
     * POST /api/hangman/games/{gameId}/end
     * 
     * @param gameId UUID of the game to end
     * @return Final game results with scores and concept details
     */
    @PostMapping("/games/{gameId}/end")
    fun endGame(@PathVariable gameId: UUID): ResponseEntity<HangmanGameResultDto> {
        logger.info("POST /api/hangman/games/$gameId/end - Ending game")
        
        val result = hangmanService.endHangmanGame(gameId)
        
        logger.info("Game ended: finalScore=${result.finalScore}, conceptsCompleted=${result.conceptsCompleted}/${result.totalConcepts}")
        return ResponseEntity.ok(result)
    }
}
