package org.duocuc.capstonebackend.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "usuarios")
data class User (
    @Id
    @GeneratedValue
    val id: UUID? = null,

    @Column(nullable = false, length = 100)
    val name: String,

    @Column(nullable = false, length = 100)
    val lastName: String,

    // many-to-one relationship with Role
    @ManyToOne(fetch = FetchType.LAZY) // Lazy loding
    @JoinColumn(name = "id_rol", nullable = false)
    val role: Role,

    @Column(nullable = false, unique = true, length = 100)
    val email: String,

    @Column(nullable = false)
    val active: Boolean = true,

    @Column(length = 20)
    val phone: String? = null,


    @Column(name = "password_hash", nullable = false, length = 255) // Store hashed passwords
    val passwordHash: String,

    @Column(name = "fecha_creacion", nullable = false)
    val creationDate: LocalDate = LocalDate.now(), // Default to current date

    @Column(name = "fecha_ultimo_login")
    val lastDateLogin: LocalDate? = null // Nullable and null by default
)