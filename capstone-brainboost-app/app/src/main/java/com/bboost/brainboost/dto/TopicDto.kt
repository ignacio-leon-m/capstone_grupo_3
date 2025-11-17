package com.bboost.brainboost.dto

import java.util.UUID

data class TopicDto(
    val id: UUID,
    val name: String,
    val subjectId: UUID
)