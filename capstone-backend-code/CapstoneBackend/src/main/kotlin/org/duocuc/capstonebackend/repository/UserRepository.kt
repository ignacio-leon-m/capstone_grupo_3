package org.duocuc.capstonebackend.repository

import org.duocuc.capstonebackend.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.List
import java.util.Optional
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): Optional<User>
    fun findUsersByRoleName(roleName: String): List<User>
    fun deleteUserByEmail(email: String)


}
