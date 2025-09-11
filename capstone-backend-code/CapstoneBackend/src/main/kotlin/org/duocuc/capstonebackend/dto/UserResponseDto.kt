package org.duocuc.capstonebackend.dto

import org.duocuc.capstonebackend.model.Role
import java.util.UUID

data class UserResponseDto(
    val id: UUID?,
    val name: String,
    val lastName: String,
    val email: String,
    val role: Role
    )
