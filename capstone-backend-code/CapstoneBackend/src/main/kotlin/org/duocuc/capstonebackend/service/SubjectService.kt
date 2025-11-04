package org.duocuc.capstonebackend.service

import org.duocuc.capstonebackend.dto.SubjectDto
import org.duocuc.capstonebackend.dto.TopicDto
import org.duocuc.capstonebackend.model.Subject
import org.duocuc.capstonebackend.model.Topic
import org.duocuc.capstonebackend.repository.SubjectRepository
import org.duocuc.capstonebackend.repository.TopicRepository
import org.duocuc.capstonebackend.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.*
import org.slf4j.LoggerFactory

@Service
class SubjectService(
    private val subjectRepository: SubjectRepository,
    private val topicRepository: TopicRepository,
    private val userRepository: UserRepository
) {

    private val log = LoggerFactory.getLogger(SubjectService::class.java)

    // MÉTODO MODIFICADO: Ahora cualquier profesor puede ver todas las asignaturas
    fun getMySubjects(): List<SubjectDto> {
        val professorId = getCurrentProfessorId()
        log.info("Buscando todas las asignaturas para profesor ID: $professorId")

        // Obtener todas las asignaturas sin filtrar por carrera
        val subjects = subjectRepository.findAllSubjects()

        log.info("Encontradas ${subjects.size} asignaturas (todas las disponibles)")

        return subjects.map { it.toDto() }
    }

    // MÉTODO ALTERNATIVO: Si quieres mantener ambas opciones
    fun getAllSubjectsForProfessors(): List<SubjectDto> {
        val professorId = getCurrentProfessorId()
        log.info("Buscando todas las asignaturas para profesor ID: $professorId")

        // Obtener todas las asignaturas
        val subjects = subjectRepository.findAllSubjects()
        log.info("Encontradas ${subjects.size} asignaturas para el profesor")

        return subjects.map { it.toDto() }
    }

    //  MÉTODO OPCIONAL: Si quieres mantener la funcionalidad original pero con otro nombre
    fun getSubjectsByProfessorDegree(): List<SubjectDto> {
        val professorId = getCurrentProfessorId()
        log.info("Buscando asignaturas de la carrera del profesor ID: $professorId")

        val professor = userRepository.findById(professorId)
            .orElseThrow { IllegalArgumentException("Usuario no encontrado con ID: $professorId") }

        // Obtener asignaturas de la carrera del profesor (comportamiento original)
        val professorDegreeId = professor.degree.id!!
        val subjects = subjectRepository.findByDegreeId(professorDegreeId)

        log.info("Encontradas ${subjects.size} asignaturas para la carrera ${professor.degree.name}")

        return subjects.map { it.toDto() }
    }

    fun getAllSubjects(): List<SubjectDto> {
        log.info("Obteniendo todas las asignaturas (modo admin)")
        val subjects = subjectRepository.findAllSubjects()
        return subjects.map { it.toDto() }
    }

    fun getTopicsBySubject(subjectId: UUID): List<TopicDto> {
        log.info("Buscando temas para asignatura ID: $subjectId")
        val topics = topicRepository.findBySubjectId(subjectId)
        log.info("Encontrados ${topics.size} temas")
        return topics.map { it.toDto() }
    }

    fun findOrCreateTopic(subjectId: UUID, topicName: String): Topic {
        log.info("Buscando o creando tema: '$topicName' para asignatura ID: $subjectId")

        // Verificar que la asignatura existe
        val subject = subjectRepository.findById(subjectId)
            .orElseThrow { IllegalArgumentException("Asignatura no encontrada con ID: $subjectId") }

        // Buscar tema existente
        val existingTopic = topicRepository.findByNameAndSubjectId(topicName, subjectId)
        return if (existingTopic != null) {
            log.info("Tema existente encontrado: ${existingTopic.id}")
            existingTopic
        } else {
            // Crear nuevo tema
            val newTopic = Topic(name = topicName, subject = subject)
            val savedTopic = topicRepository.save(newTopic)
            log.info("Nuevo tema creado: ${savedTopic.id}")
            savedTopic
        }
    }

    // MÉTODO CORREGIDO: Respeta la lógica actual del UserRepository
    private fun getCurrentProfessorId(): UUID {
        val authentication = SecurityContextHolder.getContext().authentication
        log.info("Obteniendo usuario desde authentication: ${authentication.name}")

        // Verificar que la autenticación es válida
        if (!authentication.isAuthenticated) {
            throw IllegalArgumentException("Usuario no autenticado")
        }

        val userEmail = authentication.name

        // Validar formato de email
        if (!userEmail.contains("@")) {
            throw IllegalArgumentException("Formato de email inválido en el token: $userEmail")
        }

        try {
            // Buscar usuario por email usando el método existente del repository
            val userOptional = userRepository.findByEmail(userEmail)
            val user = userOptional.orElseThrow {
                IllegalArgumentException("Usuario no encontrado con email: $userEmail")
            }

            // Verificar que el usuario es profesor o admin usando el método existente
            val professorOptional = userRepository.findProfessorById(user.id!!)
            val professor = professorOptional.orElseThrow {
                IllegalArgumentException("El usuario no tiene permisos de profesor. Rol actual: ${user.role.name}")
            }

            log.info("Usuario autorizado: ID=${professor.id}, Email=${professor.email}, Rol=${professor.role.name}")
            return professor.id!!

        } catch (e: Exception) {
            log.error("Error obteniendo ID del profesor: ${e.message}")
            throw IllegalArgumentException("No se pudo obtener el ID del profesor: ${e.message}")
        }
    }

    // --- MÉTODOS DE EXTENSIÓN PARA CONVERSIÓN A DTO ---

    private fun Subject.toDto(): SubjectDto = SubjectDto(
        id = this.id!!,
        nombre = this.nombre,
        carreraNombre = this.degree.name,
        semestres = this.semesters.map { it.name }
    )

    private fun Topic.toDto(): TopicDto = TopicDto(
        id = this.id!!,
        nombre = this.name,
        idAsignatura = this.subject.id!!,
        nombreAsignatura = this.subject.nombre
    )
}