package com.bboost.brainboost.dto

data class AiConceptsResponse(
    val inserted: Int,
    val examples: List<ConceptExampleDto> = emptyList()
)

data class ConceptExampleDto(
    val word: String,
    val hint: String?
)
