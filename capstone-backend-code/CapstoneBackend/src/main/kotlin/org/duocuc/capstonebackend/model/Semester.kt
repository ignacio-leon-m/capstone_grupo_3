package org.duocuc.capstonebackend.model

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.*

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

    // Relación ManyToMany con User (profesores)
    @ManyToMany
    @JoinTable(
        name = "profesores_semestre", // Necesitamos esta tabla intermedia
        joinColumns = [JoinColumn(name = "id_semestre")],
        inverseJoinColumns = [JoinColumn(name = "id_profesor")]
    )
    var professors: MutableList<User> = mutableListOf()
}