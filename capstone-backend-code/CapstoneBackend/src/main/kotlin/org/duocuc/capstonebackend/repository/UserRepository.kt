package org.duocuc.capstonebackend.repository

import org.duocuc.capstonebackend.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository: JpaRepository<User, UUID> {
    fun findByEmail(email: String): Optional<User>

    fun findUsersByRoleName(roleName: String): List<User>

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM User u WHERE u.role.name = :roleName")
    fun existsByRoleName(roleName: String): Boolean

    // Nuevo método para verificar si un usuario es profesor/admin
    @Query("SELECT u FROM User u WHERE u.id = :userId AND u.role.name IN ('profesor', 'admin')")
    fun findProfessorById(@Param("userId") userId: UUID): Optional<User>
}