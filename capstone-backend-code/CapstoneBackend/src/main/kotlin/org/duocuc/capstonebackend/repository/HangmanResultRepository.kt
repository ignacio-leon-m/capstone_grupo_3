package org.duocuc.capstonebackend.repository

import org.duocuc.capstonebackend.model.HangmanResult
import org.duocuc.capstonebackend.model.Game
import org.duocuc.capstonebackend.model.Concept
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface HangmanResultRepository : JpaRepository<HangmanResult, UUID> {
    fun findByGame(game: Game): List<HangmanResult>
    fun findByGameId(gameId: UUID): List<HangmanResult>
    fun findByConcept(concept: Concept): List<HangmanResult>
    fun findByGuessed(guessed: Boolean): List<HangmanResult>
    fun existsByGameAndConcept(game: Game, concept: Concept): Boolean
}
