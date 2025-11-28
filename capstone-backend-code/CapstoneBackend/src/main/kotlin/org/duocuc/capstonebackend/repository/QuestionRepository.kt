package org.duocuc.capstonebackend.repository

import org.duocuc.capstonebackend.model.Question
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface QuestionRepository : JpaRepository<Question, UUID> {
    fun findByTopicId(topicId: UUID): List<Question>
    fun findBySubjectId(subjectId: UUID): List<Question>
}
