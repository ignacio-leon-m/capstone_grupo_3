package org.duocuc.capstonebackend.model


import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.UuidGenerator
import java.util.UUID

@Entity
@Table(name = "semestres")
class Semester(
    @Column(name = "nombre", nullable = false, unique = true, length = 50)
    var name: String
) {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    var id: UUID? = null
}