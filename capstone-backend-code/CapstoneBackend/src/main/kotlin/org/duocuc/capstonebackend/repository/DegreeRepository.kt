package org.duocuc.capstonebackend.repository

import org.duocuc.capstonebackend.model.Degree
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface DegreeRepository: JpaRepository<Degree, UUID> {
    fun findByName(name: String): Degree?
}