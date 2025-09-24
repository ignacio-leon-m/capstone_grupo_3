package org.duocuc.capstone_backend.dto

data class CreateUserRequestDto(
    val name: String,
    val lastName: String,
    val email: String,
    val phone: String?,
    val password: String,
    val roleName: String = "ROLE_ALUMNO"   // p.ej.: ROLE_ADMIN / ROLE_PROFESOR / ROLE_ALUMNO
)