package org.duocuc.capstonebackend.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "materias")
class Subject (
    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "nombre", nullable = false, length = 100)
    val name: String = "",

    @ManyToOne
    @JoinColumn(name = "id_carrera", nullable = false)
    val degree: Degree
)