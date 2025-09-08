package org.duocuc.capstonebackend.repository

import org.duocuc.capstonebackend.dto.LoginRequestDto
import org.duocuc.capstonebackend.dto.SaveUserDto
import org.duocuc.capstonebackend.model.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface RoleRepository: JpaRepository<Role, UUID> {
}