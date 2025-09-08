package org.duocuc.capstonebackend.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date
import java.util.UUID

@Entity
@Table(name = "usuarios")
data class User (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(), // UUID primary key

    @Column(name = "nombre", nullable = false, length = 100)
    val firstName: String,

    @Column(name = "apellido", nullable = false, length = 100)
    val lastName: String,

    // many-to-one relationship with Role
    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    val idRole: Role,

    @Column(name = "correo", nullable = false, unique = true, length = 100)
    val email: String,

    @Column(nullable = false)
    val state: Boolean,

    @Column(name = "celular", length = 20)
    val phone: String? = null,

    @Column(name = "password_hash", nullable = false, length = 255) // Store hashed passwords
    val passwordHash: String,

    @Column(name = "fecha_creacion")
    @Temporal(TemporalType.DATE)
    val createdAt: Date = Date(),

    @Column(name = "fecha_ultimo_login")
    @Temporal(TemporalType.DATE)
    var lastLoginAt: Date?
)