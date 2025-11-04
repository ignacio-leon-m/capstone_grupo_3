package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.dto.SubjectDto
import org.duocuc.capstonebackend.dto.TopicDto
import org.duocuc.capstonebackend.service.SubjectService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*
import org.slf4j.LoggerFactory

@RestController
@RequestMapping("/api/subjects")
class SubjectController(
    private val subjectService: SubjectService
) {

    private val log = LoggerFactory.getLogger(SubjectController::class.java)

    @GetMapping("/my-subjects")
    @PreAuthorize("hasAnyAuthority('profesor','admin')")
    fun getMySubjects(): List<SubjectDto> {
        log.info("Solicitando asignaturas del profesor")
        return subjectService.getMySubjects()
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('admin')")
    fun getAllSubjects(): List<SubjectDto> {
        log.info("Solicitando todas las asignaturas (admin)")
        return subjectService.getAllSubjects()
    }

    @GetMapping("/topics/{subjectId}")
    @PreAuthorize("hasAnyAuthority('profesor','admin')")
    fun getTopicsBySubject(@PathVariable subjectId: UUID): List<TopicDto> {
        log.info("Solicitando temas para asignatura: $subjectId")
        return subjectService.getTopicsBySubject(subjectId)
    }

    // ✅ OPCIÓN 1: Usar @ModelAttribute para form-data
    @PostMapping(value = ["/topics"], consumes = ["multipart/form-data"])
    @PreAuthorize("hasAnyAuthority('profesor','admin')")
    fun createTopicFormData(@ModelAttribute request: CreateTopicFormRequest): TopicDto {
        log.info("Creando tema (Form-Data): '${request.topicName}' para asignatura ID: ${request.subjectId}")

        val subjectId = try {
            UUID.fromString(request.subjectId)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("ID de asignatura inválido: ${request.subjectId}")
        }

        val topic = subjectService.findOrCreateTopic(subjectId, request.topicName)

        return TopicDto(
            id = topic.id!!,
            nombre = topic.name,
            idAsignatura = topic.subject.id!!,
            nombreAsignatura = topic.subject.nombre
        )
    }

    // ✅ OPCIÓN 2: Usar JSON (RECOMENDADO)
    @PostMapping(value = ["/topics"], consumes = ["application/json"])
    @PreAuthorize("hasAnyAuthority('profesor','admin')")
    fun createTopicJson(@RequestBody request: CreateTopicJsonRequest): TopicDto {
        log.info("Creando tema (JSON): '${request.topicName}' para asignatura ID: ${request.subjectId}")

        val topic = subjectService.findOrCreateTopic(request.subjectId, request.topicName)

        return TopicDto(
            id = topic.id!!,
            nombre = topic.name,
            idAsignatura = topic.subject.id!!,
            nombreAsignatura = topic.subject.nombre
        )
    }

    // ✅ OPCIÓN 3: Usar parámetros de query string (más simple)
    @PostMapping(value = ["/topics-query"])
    @PreAuthorize("hasAnyAuthority('profesor','admin')")
    fun createTopicQuery(
        @RequestParam subjectId: String,
        @RequestParam topicName: String
    ): TopicDto {
        log.info("Creando tema (Query): '$topicName' para asignatura ID: $subjectId")

        val subjectUUID = try {
            UUID.fromString(subjectId)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("ID de asignatura inválido: $subjectId")
        }

        val topic = subjectService.findOrCreateTopic(subjectUUID, topicName)

        return TopicDto(
            id = topic.id!!,
            nombre = topic.name,
            idAsignatura = topic.subject.id!!,
            nombreAsignatura = topic.subject.nombre
        )
    }
}

// DTO para Form-Data
data class CreateTopicFormRequest(
    val subjectId: String,
    val topicName: String
)

// DTO para JSON
data class CreateTopicJsonRequest(
    val subjectId: UUID,
    val topicName: String
)