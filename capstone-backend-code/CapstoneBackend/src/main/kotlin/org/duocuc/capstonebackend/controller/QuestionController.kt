package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.dto.QuestionCreateDto
import org.duocuc.capstonebackend.model.Question
import org.duocuc.capstonebackend.repository.QuestionRepository
import org.duocuc.capstonebackend.repository.SubjectRepository
import org.duocuc.capstonebackend.repository.TopicRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api")
class QuestionController(
    private val questionRepository: QuestionRepository
) {

    data class QuestionDto(
        val text: String,
        val correctAnswer: String
    )

    @GetMapping("/topics/{topicId}/questions")
    fun getQuestionsByTopic(@PathVariable topicId: UUID): ResponseEntity<List<QuestionDto>> {
        val preguntas = questionRepository.findByTopicId(topicId)
            .map {
                QuestionDto(
                    text = it.text,
                    correctAnswer = it.correctAnswer
                )
            }
        return ResponseEntity.ok(preguntas)
    }

    @GetMapping("/subjects/{subjectId}/questions")
    fun getQuestionsBySubject(@PathVariable subjectId: UUID): ResponseEntity<List<QuestionDto>> {
        val preguntas = questionRepository.findBySubjectId(subjectId)
            .map {
                QuestionDto(
                    text = it.text,
                    correctAnswer = it.correctAnswer
                )
            }
        return ResponseEntity.ok(preguntas)
    }
}
