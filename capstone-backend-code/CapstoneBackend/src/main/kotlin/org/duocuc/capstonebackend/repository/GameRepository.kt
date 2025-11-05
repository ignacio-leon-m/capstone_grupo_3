package org.duocuc.capstonebackend.repository

import org.duocuc.capstonebackend.model.Game
import org.duocuc.capstonebackend.model.User
import org.duocuc.capstonebackend.model.Subject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface GameRepository : JpaRepository<Game, UUID> {
    fun findByUser(user: User): List<Game>
    fun findByUserId(userId: UUID): List<Game>
    fun findBySubject(subject: Subject): List<Game>
    fun findByGameName(gameName: String): List<Game>
    fun findByGameStatus(gameStatus: String): List<Game>
    
    @Query("SELECT g FROM Game g WHERE g.user.id = :userId AND g.gameStatus = :status")
    fun findByUserIdAndStatus(userId: UUID, status: String): List<Game>
}
