package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.service.UserService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController (
    private val userService: UserService
) {
    fun findUserByEmail(email: String) {
        userService.getUserByEmail(email)
    }
}