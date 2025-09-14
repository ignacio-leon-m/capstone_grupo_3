package org.duocuc.capstonebackend.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.Version
import org.hibernate.annotations.UuidGenerator
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "usuarios")
class User (
    @Id
    @UuidGenerator
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val id: UUID? = null, // UUID primary key

    @Column(name = "nombre", nullable = false, length = 100)
    val firstName: String,

    @Column(name = "apellido", nullable = false, length = 100)
    val lastName: String,

    // many-to-one relationship with Role
    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    val role: Role,

    @Column(name = "correo", nullable = false, unique = true, length = 100)
    val email: String,

    @Column(name = "estado",  nullable = false)
    val state: Boolean,

    @Column(name = "celular", length = 20)
    val phone: String? = null,

    @Column(name = "password_hash", nullable = false, length = 255) // Store hashed passwords
    val passwordHash: String,

    @Column(name = "fecha_creacion")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "fecha_ultimo_login")
    var lastLoginAt: LocalDateTime? = null,

    @Version
    var version: Long? = null

)