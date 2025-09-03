package org.duocuc.capstonebackend.service

import org.duocuc.capstonebackend.model.User
import org.duocuc.capstonebackend.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService (
    private val userRepository: UserRepository
) {
    fun getUserByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }
}