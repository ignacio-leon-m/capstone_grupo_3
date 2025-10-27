package com.bboost.brainboost.dto

import java.util.UUID // Podemos usar UUID o String, Retrofit/Gson lo maneja

data class UserResponseDto(
    val id: UUID?, // Coincide con tu backend
    val name: String,
    val lastName: String,
    val email: String,
    val role: RoleDto
)