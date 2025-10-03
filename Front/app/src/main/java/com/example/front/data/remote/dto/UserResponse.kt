package com.example.front.data.remote.dto

data class RoleDto(val name: String)
data class UserResponse(
    val email: String,
    val role: RoleDto
)
