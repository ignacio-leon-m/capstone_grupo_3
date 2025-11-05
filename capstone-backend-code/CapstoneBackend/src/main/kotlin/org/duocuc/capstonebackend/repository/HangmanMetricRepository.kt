package org.duocuc.capstonebackend.repository

import org.duocuc.capstonebackend.model.HangmanMetric
import org.duocuc.capstonebackend.model.Game
import org.duocuc.capstonebackend.model.Concept
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface HangmanMetricRepository : JpaRepository<HangmanMetric, UUID> {
    fun findByGame(game: Game): List<HangmanMetric>
    fun findByGameId(gameId: UUID): List<HangmanMetric>
    fun findByConcept(concept: Concept): List<HangmanMetric>
    fun findByGameAndConcept(game: Game, concept: Concept): List<HangmanMetric>
    fun findByIsCorrect(isCorrect: Boolean): List<HangmanMetric>
}
