package org.duocuc.capstone_backend.model

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "usuarios")
class User(

    @Id
    @UuidGenerator
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    var id: UUID? = null,

    @Column(name = "nombre", nullable = false, length = 100)
    var firstName: String,

    @Column(name = "apellido", nullable = false, length = 100)
    var lastName: String,

    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    var role: Role,

    @Column(name = "correo", nullable = false, unique = true, length = 100)
    var email: String,

    @Column(name = "estado", nullable = false)
    var state: Boolean = true,

    @Column(name = "celular", length = 20)
    var phone: String? = null,

    @Column(name = "password_hash", nullable = false, length = 255)
    var passwordHash: String,

    @Column(name = "fecha_creacion", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "fecha_ultimo_login")
    var lastLoginAt: LocalDateTime? = null,

    @Version
    var version: Long? = null
)
