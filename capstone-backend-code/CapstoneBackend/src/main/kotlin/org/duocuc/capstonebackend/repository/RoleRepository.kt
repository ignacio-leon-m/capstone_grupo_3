package org.duocuc.capstonebackend.repository

import org.duocuc.capstonebackend.model.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RoleRepository: JpaRepository<Role, UUID> {
}