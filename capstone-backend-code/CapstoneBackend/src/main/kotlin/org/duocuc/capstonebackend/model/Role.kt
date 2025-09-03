package org.duocuc.capstonebackend.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table


enum class RoleType {
    ADMIN,
    STUDENT,
    TEACHER
}

@Entity
@Table(name = "roles")
data class Role (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // UUID
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "nombre_rol", nullable = false, length = 50)
    val roleName: RoleType
)