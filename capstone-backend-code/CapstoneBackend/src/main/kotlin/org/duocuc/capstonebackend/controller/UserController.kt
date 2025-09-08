package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.model.User
import org.duocuc.capstonebackend.service.AuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController (
    private val authService: AuthService,
) {
    // Endpoint to create a new user
    @PostMapping
    fun createUser(@RequestBody user: User){

    }
}