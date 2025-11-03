package org.duocuc.capstonebackend.controller

import org.duocuc.capstonebackend.dto.StudentRequestDto
import org.duocuc.capstonebackend.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/users")
class UserController (
    private val userService: UserService,
){

    @GetMapping("/students")
    fun getAllStudents(): List<StudentRequestDto> {
        val students = userService.getAllStudents()
        return students
    }

    @GetMapping("/professors")
    fun getAllProfessors(): List<StudentRequestDto> {
        val professors = userService.getAllProfessors()
        return professors
    }
}