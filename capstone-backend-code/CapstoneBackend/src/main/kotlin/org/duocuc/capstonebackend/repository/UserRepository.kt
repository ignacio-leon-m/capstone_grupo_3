package org.duocuc.capstonebackend.repository

import org.duocuc.capstonebackend.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface UserRepository: JpaRepository<User, UUID> {
    fun findByEmail(email: String): Optional<User>
    fun findByRut(rut: String): Optional<User>
    fun findUsersByRoleName(roleName: String): kotlin.collections.MutableList<org.duocuc.capstonebackend.model.User>
}
