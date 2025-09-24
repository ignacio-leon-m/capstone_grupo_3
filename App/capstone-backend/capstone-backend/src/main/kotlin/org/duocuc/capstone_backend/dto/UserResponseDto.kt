package org.duocuc.capstone_backend.dto

import org.duocuc.capstone_backend.model.Role
import java.util.UUID

data class UserResponseDto(
    val id: UUID?,
    val name: String,
    val lastName: String,
    val email: String,
    val role: Role
)
