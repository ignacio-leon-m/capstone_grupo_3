package org.duocuc.capstonebackend.repository

import org.duocuc.capstonebackend.model.Institution
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface InstitutionRepository : JpaRepository<Institution, UUID> {
    fun findByName(name: String): Institution?
}
