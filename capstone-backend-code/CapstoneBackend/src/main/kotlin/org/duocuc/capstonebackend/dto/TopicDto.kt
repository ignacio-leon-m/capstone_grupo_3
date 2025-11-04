package org.duocuc.capstonebackend.dto

import java.util.UUID

data class TopicDto(
    val id: UUID,
    val nombre: String,
    val idAsignatura: UUID,
    val nombreAsignatura: String? = null
)