package org.duocuc.capstonebackend.service

import org.duocuc.capstonebackend.dto.*
import org.duocuc.capstonebackend.exception.BadRequestException
import org.duocuc.capstonebackend.exception.ResourceNotFoundException
import org.duocuc.capstonebackend.model.HangmanMetric
import org.duocuc.capstonebackend.model.HangmanResult
import org.duocuc.capstonebackend.repository.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class HangmanService(
    private val gameService: GameService,
    private val gameRepository: GameRepository,
    private val conceptRepository: ConceptRepository,
    private val hangmanMetricRepository: HangmanMetricRepository,
    private val hangmanResultRepository: HangmanResultRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        const val MIN_CONCEPTS = 10
        const val MAX_CONCEPTS = 12
        const val POINTS_PER_CONCEPT = 1 // 1 punto por concepto completado
    }

    @Transactional
    fun startHangmanGame(request: HangmanGameStartDto): HangmanGameStateDto {
        logger.info("Starting Hangman game for user ${request.userId}, subject ${request.subjectId}, topic ${request.topicId}")

        // Validate that topic exists and has enough concepts
        val availableConcepts = conceptRepository.findByTopicId(request.topicId)
        if (availableConcepts.size < MIN_CONCEPTS) {
            throw BadRequestException("Topic must have at least $MIN_CONCEPTS concepts. Found: ${availableConcepts.size}")
        }

        // Select 10-12 random concepts
        val selectedConcepts = availableConcepts.shuffled().take(MAX_CONCEPTS)
        logger.debug("Selected ${selectedConcepts.size} random concepts for Hangman game")

        // Create game using GameService
        val gameStartDto = GameStartDto(
            userId = request.userId,
            subjectId = request.subjectId,
            gameName = "hangman"
        )
        val gameResponse = gameService.startGame(gameStartDto)

        return HangmanGameStateDto(
            gameId = gameResponse.id,
            userId = request.userId,
            subjectId = request.subjectId,
            concepts = selectedConcepts.map { it.toHangmanConceptDto() },
            currentConceptIndex = 0,
            livesRemaining = gameResponse.attemptsRemaining ?: 3,
            gameStatus = gameResponse.gameStatus,
            score = 0
        )
    }

    @Transactional
    fun attemptLetter(gameId: UUID, request: HangmanAttemptDto): HangmanAttemptResponseDto {
        logger.debug("Processing letter attempt for game $gameId: letter=${request.letter}, conceptId=${request.conceptId}")

        val game = gameRepository.findById(gameId)
            .orElseThrow { ResourceNotFoundException("Game not found with ID: $gameId") }

        if (game.gameStatus != "activo") {
            throw BadRequestException("Game is not active")
        }

        val concept = conceptRepository.findById(request.conceptId)
            .orElseThrow { ResourceNotFoundException("Concept not found with ID: ${request.conceptId}") }

        // ✅ VALIDATION 1: Verify it's a letter (alphabetic only)
        val inputLetter = request.letter.trim()
        if (inputLetter.length != 1 || !inputLetter[0].isLetter()) {
            // ❌ Special characters or numbers: PENALIZED
            val currentLives = game.attemptsRemaining ?: 0
            if (currentLives > 0) {
                game.attemptsRemaining = currentLives - 1
                gameRepository.save(game)
                logger.warn("Invalid character attempted: '$inputLetter'. Lives remaining: ${game.attemptsRemaining}")
            }
            
            throw BadRequestException("Only letters are allowed. Lives remaining: ${game.attemptsRemaining ?: 0}")
        }

        val letter = inputLetter.lowercase()
        val letterChar = letter[0]
        
        // ✅ VALIDATION 2: Check if letter was already used (check previous metrics)
        val previousAttempts = hangmanMetricRepository.findByGameAndConcept(game, concept)
        val usedLetters = previousAttempts.map { it.attemptedLetter }.toSet()
        
        if (letterChar in usedLetters) {
            // ❌ Repeated letter: NOT penalized, just inform
            logger.debug("Letter '$letter' already attempted for this concept")
            throw BadRequestException("You already tried letter '$letter'. Used letters: ${usedLetters.joinToString(", ")}")
        }

        // Check if letter is in the word (case-insensitive)
        val word = concept.word.lowercase()
        val isCorrect = word.contains(letter)

        // Find all positions of the letter
        val positions = if (isCorrect) {
            word.indices.filter { word[it].toString() == letter }
        } else {
            emptyList()
        }

        // Save granular metric
        val metric = HangmanMetric(
            game = game,
            user = game.user,
            concept = concept,
            attemptedLetter = letterChar,
            isCorrect = isCorrect,
            letterPosition = if (positions.isNotEmpty()) positions.first() else null,
            responseTimeMs = request.responseTimeMs?.toInt()
        )
        hangmanMetricRepository.save(metric)

        // If letter is incorrect, reduce lives (3 TOTAL for entire game)
        val currentLives = game.attemptsRemaining ?: 0
        if (!isCorrect && currentLives > 0) {
            game.attemptsRemaining = currentLives - 1
            gameRepository.save(game)
            logger.info("Incorrect letter '$letter'. Lives remaining: ${game.attemptsRemaining}")
        }

        return HangmanAttemptResponseDto(
            isCorrect = isCorrect,
            positions = positions,
            livesRemaining = game.attemptsRemaining ?: 0,
            gameOver = (game.attemptsRemaining ?: 0) == 0
        )
    }

    @Transactional
    fun submitConceptResult(gameId: UUID, conceptId: UUID, guessed: Boolean, timeMs: Long): HangmanConceptResultDto {
        logger.info("Submitting result for game $gameId, concept $conceptId: guessed=$guessed, timeMs=$timeMs")

        val game = gameRepository.findById(gameId)
            .orElseThrow { ResourceNotFoundException("Game not found with ID: $gameId") }

        val concept = conceptRepository.findById(conceptId)
            .orElseThrow { ResourceNotFoundException("Concept not found with ID: $conceptId") }

        // Verify that result doesn't already exist for this concept in this game
        if (hangmanResultRepository.existsByGameAndConcept(game, concept)) {
            throw BadRequestException("Result already exists for this concept in this game")
        }

        // Calculate attempts used (count metrics for this concept in this game)
        val metrics = hangmanMetricRepository.findByGameAndConcept(game, concept)
        val attemptsUsed = metrics.size

        // Calculate score (1 point if word was guessed)
        val scoreObtained = if (guessed) java.math.BigDecimal(POINTS_PER_CONCEPT) else java.math.BigDecimal.ZERO

        // Save result
        val result = HangmanResult(
            game = game,
            concept = concept,
            guessed = guessed,
            attemptsUsed = attemptsUsed,
            totalTimeMs = timeMs.toInt(),
            scoreObtained = scoreObtained,
            livesRemaining = game.attemptsRemaining
        )
        hangmanResultRepository.save(result)

        logger.info("Result saved: scoreObtained=$scoreObtained, attemptsUsed=$attemptsUsed")

        return HangmanConceptResultDto(
            conceptId = conceptId,
            word = concept.word,
            guessed = guessed,
            attemptsUsed = attemptsUsed,
            totalTimeMs = timeMs,
            scoreObtained = scoreObtained.toInt(),
            livesRemaining = game.attemptsRemaining ?: 0
        )
    }

    fun getGameProgress(gameId: UUID): HangmanGameStateDto {
        logger.debug("Fetching game progress for game $gameId")

        val game = gameRepository.findById(gameId)
            .orElseThrow { ResourceNotFoundException("Game not found with ID: $gameId") }

        // Get all game results
        val results = hangmanResultRepository.findByGame(game)
        
        // Calculate total score
        val totalScore = results.mapNotNull { it.scoreObtained }.fold(java.math.BigDecimal.ZERO) { acc, value -> acc.add(value) }

        // Get game concepts (from unique metrics)
        val gameMetrics = hangmanMetricRepository.findByGame(game)
        val conceptsInGame = gameMetrics.map { it.concept }.distinctBy { it.id }

        return HangmanGameStateDto(
            gameId = game.id!!,
            userId = game.user.id!!,
            subjectId = game.subject.id!!,
            concepts = conceptsInGame.map { it.toHangmanConceptDto() },
            currentConceptIndex = results.size,
            livesRemaining = game.attemptsRemaining ?: 0,
            gameStatus = game.gameStatus,
            score = totalScore.toInt()
        )
    }

    @Transactional
    fun endHangmanGame(gameId: UUID): HangmanGameResultDto {
        logger.info("Ending Hangman game $gameId")

        val game = gameRepository.findById(gameId)
            .orElseThrow { ResourceNotFoundException("Game not found with ID: $gameId") }

        if (game.gameStatus != "activo") {
            throw BadRequestException("Game is not active")
        }

        // Get all results
        val results = hangmanResultRepository.findByGame(game)
        val totalScore = results.mapNotNull { it.scoreObtained }.fold(java.math.BigDecimal.ZERO) { acc, value -> acc.add(value) }

        // End game using GameService
        val endDto = GameEndDto(gameId = gameId, finalScore = totalScore.toString())
        gameService.endGame(gameId, endDto)

        logger.info("Hangman game $gameId finalized. Total score: $totalScore")

        return HangmanGameResultDto(
            gameId = gameId,
            finalScore = totalScore.toInt(),
            conceptsCompleted = results.count { it.guessed },
            totalConcepts = results.size,
            livesRemaining = game.attemptsRemaining ?: 0,
            conceptResults = results.map { it.toConceptResultDto() }
        )
    }

    private fun org.duocuc.capstonebackend.model.Concept.toHangmanConceptDto() = HangmanConceptDto(
        id = id!!,
        word = word,
        hint = hint
    )

    private fun HangmanResult.toConceptResultDto() = HangmanConceptResultDto(
        conceptId = concept.id!!,
        word = concept.word,
        guessed = guessed,
        attemptsUsed = attemptsUsed,
        totalTimeMs = (totalTimeMs ?: 0).toLong(),
        scoreObtained = (scoreObtained ?: java.math.BigDecimal.ZERO).toInt(),
        livesRemaining = livesRemaining ?: 0
    )
}
