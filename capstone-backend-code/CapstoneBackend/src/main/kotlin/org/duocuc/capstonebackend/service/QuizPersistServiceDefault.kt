// QuizPersistServiceDefault.kt
package org.duocuc.capstonebackend.service

import org.duocuc.capstonebackend.dto.SaveQuizRequestDto
import org.duocuc.capstonebackend.dto.SaveQuizResponseDto
import org.duocuc.capstonebackend.model.Question
import org.duocuc.capstonebackend.model.Subject
import org.duocuc.capstonebackend.model.Topic
import org.duocuc.capstonebackend.repository.QuestionRepository
import org.duocuc.capstonebackend.repository.SubjectRepository
import org.duocuc.capstonebackend.repository.TopicRepository
import org.duocuc.capstonebackend.service.QuizPersistService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuizPersistServiceDefault(
    private val subjectRepo: SubjectRepository,
    private val topicRepo: TopicRepository,
    private val questionRepo: QuestionRepository
) : QuizPersistService {

    private val log = LoggerFactory.getLogger(QuizPersistServiceDefault::class.java)

    @Transactional
    override fun saveQuiz(req: SaveQuizRequestDto): SaveQuizResponseDto {
        val subject: Subject = subjectRepo.findById(req.subjectId)
            .orElseThrow { IllegalArgumentException("Asignatura no encontrada: ${req.subjectId}") }

        // 1) Buscar/crear Topic y asegurarnos que tenga ID asignado (flush)
        var topic = topicRepo.findByNameAndSubject(req.topicName, subject)
        if (topic == null) {
            topic = topicRepo.save(Topic(name = req.topicName, subject = subject))
            topicRepo.flush() // ← garantiza que topic.id no sea null
            log.info("Topic creado: {} ({})", topic.name, topic.id)
        } else {
            log.info("Topic reutilizado: {} ({})", topic.name, topic.id)
        }

        // 2) Mapear preguntas asegurando topic seteado
        val toSave = req.questions.map { q ->
            val correcta = q.options[q.answerIndex]
            Question(
                text = q.question,
                correctAnswer = correcta,
                subject = subject,
                topic = topic
            )
        }

        val saved = questionRepo.saveAll(toSave).size
        log.info("Preguntas guardadas: {}", saved)
        return SaveQuizResponseDto(saved = saved)
    }
}