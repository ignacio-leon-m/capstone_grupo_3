package com.bboost.brainboost.dto

data class AiQueryResponseDto(
    val documentId: String?,
    val fileName: String?,
    val prompt: String?,
    val response: String
)
