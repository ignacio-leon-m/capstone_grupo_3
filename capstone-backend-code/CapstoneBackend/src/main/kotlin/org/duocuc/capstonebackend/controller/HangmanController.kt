package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.dto.*
import org.duocuc.capstonebackend.service.HangmanService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/hangman")
class HangmanController(
    private val hangmanService: HangmanService
) {

//    Start a new Hangman game.
    @PostMapping("/start")
    fun startGame(@RequestBody request: HangmanGameStartDto): ResponseEntity<HangmanGameStateDto> {
        val gameState = hangmanService.startHangmanGame(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(gameState)
    }

//    Current game state
    @GetMapping("/games/{gameId}")
    fun getGameProgress(@PathVariable gameId: UUID): ResponseEntity<HangmanGameStateDto> {
        val gameState = hangmanService.getGameProgress(gameId)
        return ResponseEntity.ok(gameState)
    }

//    Attempt a letter for a specific concept.
    @PostMapping("/games/{gameId}/attempt")
    fun attemptLetter(
        @PathVariable gameId: UUID,
        @RequestBody request: HangmanAttemptDto
    ): ResponseEntity<HangmanAttemptResponseDto> {
        val response = hangmanService.attemptLetter(gameId, request)
        return ResponseEntity.ok(response)
    }

//    Create new scratch file from selection
    @PostMapping("/games/{gameId}/concepts/submit")
    fun submitConceptResult(
        @PathVariable gameId: UUID,
        @RequestBody request: HangmanConceptSubmitDto
    ): ResponseEntity<HangmanConceptResultDto> {

        val result = hangmanService.submitConceptResult(
            gameId = gameId,
            conceptId = request.conceptId,
            guessed = request.guessed,
            timeMs = request.timeMs
        )
        
        return ResponseEntity.ok(result)
    }

//    End/finalize a Hangman game.
    @PostMapping("/games/{gameId}/end")
    fun endGame(@PathVariable gameId: UUID): ResponseEntity<HangmanGameResultDto> {
        val result = hangmanService.endHangmanGame(gameId)
        return ResponseEntity.ok(result)
    }
}
