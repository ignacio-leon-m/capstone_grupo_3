package org.duocuc.capstone_backend.repository

import org.duocuc.capstone_backend.model.Carrera
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CarreraRepository : JpaRepository<Carrera, UUID>
