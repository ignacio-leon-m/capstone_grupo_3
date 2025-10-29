package org.duocuc.capstonebackend.nosql

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.UUID

interface AiSummaryRepository : MongoRepository<AiSummaryDoc, String> {
    fun findTop20ByUserIdOrderByCreatedAtDesc(userId: UUID): List<AiSummaryDoc>
}
