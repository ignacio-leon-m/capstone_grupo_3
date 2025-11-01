package org.duocuc.capstonebackend.dto

import java.time.LocalDateTime

data class StudentRequestDto (
    val rut: String,
    val name: String,
    val lastName: String,
    val email: String,
    var lastLoginAt: LocalDateTime? = null
)