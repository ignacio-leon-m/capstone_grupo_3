package org.duocuc.capstonebackend.dto

import java.util.UUID

data class SaveUserDto(
    val name: String,
    val lastName: String,
    val idRole: UUID,
    val email: String,
    val phone: String?,
    val password: String
)
