package org.duocuc.capstone_backend.model

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.UUID

@Entity
@Table(name = "roles")
class Role(

    @Id
    @UuidGenerator
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    var id: UUID? = null,

    @Column(name = "nombre", unique = true, nullable = false, length = 50)
    var name: String
)
