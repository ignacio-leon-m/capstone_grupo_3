package org.duocuc.capstonebackend.repository

import org.duocuc.capstonebackend.model.Subject
import org.duocuc.capstonebackend.model.Topic
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TopicRepository : JpaRepository<Topic, UUID> {
    fun findByNameAndSubject(name: String, subject: Subject): Topic?
    fun findBySubjectId(subjectId: UUID): List<Topic>
}
