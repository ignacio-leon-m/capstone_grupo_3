package org.duocuc.capstonebackend.dto

import java.util.UUID

data class RegisterRequestDto(
    val name: String,
    val lastName: String,
    val idRole: UUID,
    val email: String,
    val phone: String?,
    val password: String
)
