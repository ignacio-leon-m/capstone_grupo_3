package org.duocuc.capstonebackend.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.UuidGenerator
import java.util.UUID

@Entity
@Table(name = "asignaturas", schema = "public")
class Subject (
    @Column(name = "nombre", nullable = false, length = 100)
    var nombre: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_carrera", nullable = false,
        foreignKey = ForeignKey(name = "fk_asignatura_carrera"))
    var degree: Degree,

    // Relación ManyToMany con Semestre
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "asignaturas_semestre",
        joinColumns = [JoinColumn(name = "id_asignatura",
            foreignKey = ForeignKey(name = "fk_asig_sem_asignatura"))],
        inverseJoinColumns = [JoinColumn(name = "id_semestre",
            foreignKey = ForeignKey(name = "fk_asig_sem_semestre"))]
    )
    var semesters: MutableList<Semester> = mutableListOf()
) {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    var id: UUID? = null
}