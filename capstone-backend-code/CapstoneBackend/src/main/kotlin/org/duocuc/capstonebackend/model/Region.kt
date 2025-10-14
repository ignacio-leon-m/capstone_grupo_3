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
@Table(name = "regiones")
class Region(
    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    var name: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pais", nullable = false, foreignKey = ForeignKey(name = "fk_region_pais"))
    var country: Country
) {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    var id: UUID? = null
}