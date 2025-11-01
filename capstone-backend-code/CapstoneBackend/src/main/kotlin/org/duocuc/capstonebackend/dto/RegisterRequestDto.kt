package org.duocuc.capstonebackend.dto

data class RegisterRequestDto(
    val rut: String,
    val name: String,
    val lastName: String,
    val email: String,
    val phone: String?,
    val password: String,
    val role: String?,
    val degreeName: String?
)
