package org.duocuc.capstonebackend.repository

import org.duocuc.capstonebackend.model.Concept
import org.duocuc.capstonebackend.model.Topic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ConceptRepository : JpaRepository<Concept, UUID> {
    fun findByTopic(topic: Topic): List<Concept>
    fun findByTopicId(topicId: UUID): List<Concept>
    fun findByWordContainingIgnoreCase(word: String): List<Concept>
}
