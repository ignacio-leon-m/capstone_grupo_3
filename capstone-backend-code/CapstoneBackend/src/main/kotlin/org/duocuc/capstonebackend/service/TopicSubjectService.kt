package org.duocuc.capstonebackend.service

import jakarta.transaction.Transactional
import org.duocuc.capstonebackend.dto.TopicDto
import org.duocuc.capstonebackend.repository.SubjectRepository
import org.duocuc.capstonebackend.repository.TopicRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TopicSubjectService(
    private val topicRepository: TopicRepository,
    private val subjectRepository: SubjectRepository
) {
    @Transactional
    fun getTopicsBySubjectId(subjectId: UUID): List<TopicDto> {
        val subject = subjectRepository.findById(subjectId)
            .orElseThrow { IllegalArgumentException("Subject no encontrada con id: $subjectId") }
        val topics = topicRepository.findTopicsBySubjectId(subjectId)

        return topics.map { topic ->
            TopicDto(
                topicId = topic.topicId,
                name = topic.name,
                subjectId = topic.subjectId
            )
        }

    }

}

