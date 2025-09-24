package org.duocuc.capstone_backend.dto

data class RegisterRequestDto(
    val name: String,
    val lastName: String,
    val email: String,
    val phone: String?,
    val password: String
)
