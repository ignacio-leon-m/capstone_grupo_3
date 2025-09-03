package org.duocuc.capstonebackend.repository

import org.duocuc.capstonebackend.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByName(name: String): User?
}