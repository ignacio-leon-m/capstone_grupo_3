package org.duocuc.capstonebackend.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.UuidGenerator
import java.util.UUID

@Entity
@Table(name = "instituciones")
class Institution(
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID? = null,

    @Column(name = "nombre", nullable = false, unique = true, length = 255)
    var name: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "id_comuna", nullable = true, foreignKey = ForeignKey(name = "fk_institucion_comuna"))
    var commune: Commune? = null
)