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

        // Validar que el tema existe y tiene suficientes conceptos
        val availableConcepts = conceptRepository.findByTopicId(request.topicId)
        if (availableConcepts.size < MIN_CONCEPTS) {
            throw BadRequestException("El tema debe tener al menos $MIN_CONCEPTS conceptos. Encontrados: ${availableConcepts.size}")
        }

        // Seleccionar 10-12 conceptos aleatorios
        val selectedConcepts = availableConcepts.shuffled().take(MAX_CONCEPTS)
        logger.debug("Selected ${selectedConcepts.size} random concepts for Hangman game")

        // Crear partida usando GameService
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
            .orElseThrow { ResourceNotFoundException("Juego no encontrado con ID: $gameId") }

        if (game.gameStatus != "activo") {
            throw BadRequestException("El juego no está activo")
        }

        val concept = conceptRepository.findById(request.conceptId)
            .orElseThrow { ResourceNotFoundException("Concepto no encontrado con ID: ${request.conceptId}") }

        // ✅ VALIDACIÓN 1: Verificar que es una letra (solo alfabético)
        val inputLetter = request.letter.trim()
        if (inputLetter.length != 1 || !inputLetter[0].isLetter()) {
            // ❌ Caracteres especiales o números: PENALIZA
            val currentLives = game.attemptsRemaining ?: 0
            if (currentLives > 0) {
                game.attemptsRemaining = currentLives - 1
                gameRepository.save(game)
                logger.warn("Invalid character attempted: '$inputLetter'. Lives remaining: ${game.attemptsRemaining}")
            }
            
            throw BadRequestException("Solo se permiten letras. Vidas restantes: ${game.attemptsRemaining ?: 0}")
        }

        val letter = inputLetter.lowercase()
        val letterChar = letter[0]
        
        // ✅ VALIDACIÓN 2: Verificar si la letra ya fue usada (revisar métricas previas)
        val previousAttempts = hangmanMetricRepository.findByGameAndConcept(game, concept)
        val usedLetters = previousAttempts.map { it.attemptedLetter }.toSet()
        
        if (letterChar in usedLetters) {
            // ❌ Letra repetida: NO penaliza, solo informa
            logger.debug("Letter '$letter' already attempted for this concept")
            throw BadRequestException("Ya has intentado la letra '$letter'. Letras usadas: ${usedLetters.joinToString(", ")}")
        }

        // Verificar si la letra está en la palabra (case-insensitive)
        val word = concept.word.lowercase()
        val isCorrect = word.contains(letter)

        // Encontrar todas las posiciones de la letra
        val positions = if (isCorrect) {
            word.indices.filter { word[it].toString() == letter }
        } else {
            emptyList()
        }

        // Guardar métrica granular
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

        // Si la letra es incorrecta, reducir vidas (3 TOTALES para toda la partida)
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
            .orElseThrow { ResourceNotFoundException("Juego no encontrado con ID: $gameId") }

        val concept = conceptRepository.findById(conceptId)
            .orElseThrow { ResourceNotFoundException("Concepto no encontrado con ID: $conceptId") }

        // Verificar que no exista ya un resultado para este concepto en este juego
        if (hangmanResultRepository.existsByGameAndConcept(game, concept)) {
            throw BadRequestException("Ya existe un resultado para este concepto en este juego")
        }

        // Calcular intentos usados (contar métricas de este concepto en este juego)
        val metrics = hangmanMetricRepository.findByGameAndConcept(game, concept)
        val attemptsUsed = metrics.size

        // Calcular puntaje (1 punto si adivinó la palabra)
        val scoreObtained = if (guessed) java.math.BigDecimal(POINTS_PER_CONCEPT) else java.math.BigDecimal.ZERO

        // Guardar resultado
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
            .orElseThrow { ResourceNotFoundException("Juego no encontrado con ID: $gameId") }

        // Obtener todos los resultados del juego
        val results = hangmanResultRepository.findByGame(game)
        
        // Calcular puntaje total
        val totalScore = results.mapNotNull { it.scoreObtained }.fold(java.math.BigDecimal.ZERO) { acc, value -> acc.add(value) }

        // Obtener conceptos del juego (desde las métricas únicas)
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
            .orElseThrow { ResourceNotFoundException("Juego no encontrado con ID: $gameId") }

        if (game.gameStatus != "activo") {
            throw BadRequestException("El juego no está activo")
        }

        // Obtener todos los resultados
        val results = hangmanResultRepository.findByGame(game)
        val totalScore = results.mapNotNull { it.scoreObtained }.fold(java.math.BigDecimal.ZERO) { acc, value -> acc.add(value) }

        // Finalizar juego usando GameService
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
