package org.duocuc.capstonebackend.service

import org.duocuc.capstonebackend.dto.GameStartDto
import org.duocuc.capstonebackend.dto.GameResponseDto
import org.duocuc.capstonebackend.dto.GameEndDto
import org.duocuc.capstonebackend.exception.BadRequestException
import org.duocuc.capstonebackend.exception.ResourceNotFoundException
import org.duocuc.capstonebackend.model.Game
import org.duocuc.capstonebackend.repository.GameRepository
import org.duocuc.capstonebackend.repository.SubjectRepository
import org.duocuc.capstonebackend.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
class GameService(
    private val gameRepository: GameRepository,
    private val userRepository: UserRepository,
    private val subjectRepository: SubjectRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    companion object {
        const val INITIAL_LIVES = 3 // 3 vidas totales por partida
    }

    @Transactional
    fun startGame(request: GameStartDto): GameResponseDto {
        logger.info("Starting new game for user ${request.userId}, subject ${request.subjectId}, game ${request.gameName}")

        val user = userRepository.findById(request.userId)
            .orElseThrow { ResourceNotFoundException("Usuario no encontrado con ID: ${request.userId}") }

        val subject = subjectRepository.findById(request.subjectId)
            .orElseThrow { ResourceNotFoundException("Asignatura no encontrada con ID: ${request.subjectId}") }

        // Crear nueva partida con 3 vidas
        val game = Game(
            user = user,
            subject = subject,
            gameName = request.gameName,
            attemptsRemaining = INITIAL_LIVES,
            gameStatus = "activo",
            startDate = LocalDateTime.now(),
            endDate = null,
            score = null
        )

        val saved = gameRepository.save(game)
        logger.info("Game created with ID: ${saved.id}")

        return saved.toResponseDto()
    }

    fun getGameState(gameId: UUID): GameResponseDto {
        logger.debug("Fetching game state for game $gameId")

        val game = gameRepository.findById(gameId)
            .orElseThrow { ResourceNotFoundException("Juego no encontrado con ID: $gameId") }

        return game.toResponseDto()
    }

    @Transactional
    fun endGame(gameId: UUID, request: GameEndDto): GameResponseDto {
        logger.info("Ending game $gameId with final score ${request.finalScore}")

        val game = gameRepository.findById(gameId)
            .orElseThrow { ResourceNotFoundException("Juego no encontrado con ID: $gameId") }

        if (game.gameStatus == "finalizado") {
            throw BadRequestException("El juego ya está finalizado")
        }

        // Actualizar juego
        game.gameStatus = "finalizado"
        game.endDate = LocalDateTime.now()
        game.score = BigDecimal(request.finalScore)

        val updatedGame = gameRepository.save(game)

        // TODO: Crear registro de puntaje cuando el modelo Score esté implementado
        // val score = Score(
        //     user = game.user,
        //     subject = game.subject,
        //     score = BigDecimal(request.finalScore),
        //     enrollmentDate = LocalDate.now()
        // )
        // scoreRepository.save(score)

        logger.info("Game $gameId finalized with score ${request.finalScore}")

        return updatedGame.toResponseDto()
    }

    fun getUserGames(userId: UUID): List<GameResponseDto> {
        logger.debug("Fetching all games for user $userId")

        if (!userRepository.existsById(userId)) {
            throw ResourceNotFoundException("Usuario no encontrado con ID: $userId")
        }

        return gameRepository.findByUserId(userId).map { it.toResponseDto() }
    }

    fun getActiveGames(userId: UUID): List<GameResponseDto> {
        logger.debug("Fetching active games for user $userId")

        if (!userRepository.existsById(userId)) {
            throw ResourceNotFoundException("Usuario no encontrado con ID: $userId")
        }

        return gameRepository.findByUserIdAndStatus(userId, "activo").map { it.toResponseDto() }
    }

    private fun Game.toResponseDto() = GameResponseDto(
        id = id!!,
        gameName = gameName ?: "",
        gameStatus = gameStatus,
        attemptsRemaining = attemptsRemaining,
        score = score?.toString(),
        startDate = startDate.format(dateFormatter),
        endDate = endDate?.format(dateFormatter)
    )
}
