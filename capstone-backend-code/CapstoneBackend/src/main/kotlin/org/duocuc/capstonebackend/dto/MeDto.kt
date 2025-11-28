package org.duocuc.capstonebackend.dto

import java.util.UUID

data class MeDto(
    val id: UUID,
    val email: String,
    val name: String,
    val lastName: String,
    val role: String
)
