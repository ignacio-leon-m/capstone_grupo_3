package org.duocuc.capstonebackend.repository

import org.duocuc.capstonebackend.model.Metric
import org.duocuc.capstonebackend.model.User
import org.duocuc.capstonebackend.model.Question
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MetricRepository : JpaRepository<Metric, UUID> {
    fun findByUser(user: User): List<Metric>
    fun findByUserId(userId: UUID): List<Metric>
    fun findByQuestion(question: Question): List<Metric>
    fun findByCorrectAnswer(correctAnswer: Boolean): List<Metric>
}
