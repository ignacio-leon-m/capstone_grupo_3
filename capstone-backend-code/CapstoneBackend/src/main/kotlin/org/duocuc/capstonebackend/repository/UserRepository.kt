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
    fun findUsersByRoleName(roleName: String): kotlin.collections.MutableList<org.duocuc.capstonebackend.model.User>
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM User u WHERE u.role.name = :roleName")
    fun existsByRoleName(roleName: String): Boolean
}
