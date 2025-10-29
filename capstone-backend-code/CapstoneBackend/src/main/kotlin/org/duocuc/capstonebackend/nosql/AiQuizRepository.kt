package org.duocuc.capstonebackend.nosql

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.UUID

interface AiQuizRepository : MongoRepository<AiQuizDoc, String> {
    fun findTop20ByUserIdOrderByCreatedAtDesc(userId: UUID): List<AiQuizDoc>
}
