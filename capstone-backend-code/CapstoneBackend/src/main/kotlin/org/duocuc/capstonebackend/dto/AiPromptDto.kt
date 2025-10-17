package org.duocuc.capstonebackend.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class AiPromptDto @JsonCreator constructor(
    // La anotación @param:JsonProperty le dice a Jackson cómo mapear la clave JSON 'prompt'
    // al constructor de Kotlin.
    @param:JsonProperty("prompt") val prompt: String
)