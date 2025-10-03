package com.example.front.data.remote.dto

data class QuizDto(
    val preguntas: List<PreguntaDto>
)

data class PreguntaDto(
    val enunciado: String,
    val opciones: List<String>,
    val respuestaIndex: Int
)
