package org.duocuc.capstonebackend.repository

import org.duocuc.capstonebackend.model.Subject
import org.duocuc.capstonebackend.model.Topic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface TopicRepository : JpaRepository<Topic, UUID> {

    fun findByNameAndSubject(name: String, subject: Subject): Topic?

    // Buscar temas por ID de asignatura
    @Query("SELECT t FROM Topic t WHERE t.subject.id = :subjectId")
    fun findBySubjectId(@Param("subjectId") subjectId: UUID): List<Topic>

    // Buscar tema por nombre e ID de asignatura
    @Query("SELECT t FROM Topic t WHERE t.name = :topicName AND t.subject.id = :subjectId")
    fun findByNameAndSubjectId(
        @Param("topicName") topicName: String,
        @Param("subjectId") subjectId: UUID
    ): Topic?
}