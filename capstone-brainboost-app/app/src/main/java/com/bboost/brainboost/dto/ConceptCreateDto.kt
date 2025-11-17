package com.bboost.brainboost.dto

import java.util.UUID

data class ConceptCreateDto(
    val word: String,
    val hint: String?,
    val topicId: UUID  //
)