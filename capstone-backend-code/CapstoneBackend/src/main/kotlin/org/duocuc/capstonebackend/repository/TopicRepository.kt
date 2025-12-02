package org.duocuc.capstonebackend.repository

import org.duocuc.capstonebackend.dto.TopicDto
import org.duocuc.capstonebackend.model.Subject
import org.duocuc.capstonebackend.model.Topic
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TopicRepository : JpaRepository<Topic, UUID> {
    fun findTopicsBySubjectId(subjectId: UUID): List<TopicDto>;
}

