package org.duocuc.capstonebackend.service

import org.duocuc.capstonebackend.model.User
import org.duocuc.capstonebackend.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService (
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    // Get user by email
    fun getUserByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    // Create a new user with hashed password
    fun createUser(user: User): User {
        // Hash the password before saving
        val hashedPassword = passwordEncoder.encode(user.passwordHash)
        val userToSave = user.copy(passwordHash = hashedPassword)
        return userRepository.save(userToSave)
    }
}