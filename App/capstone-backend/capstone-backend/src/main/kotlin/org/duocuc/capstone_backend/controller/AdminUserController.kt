package org.duocuc.capstone_backend.controller

import org.duocuc.capstone_backend.dto.CreateUserRequestDto
import org.duocuc.capstone_backend.dto.UserResponseDto
import org.duocuc.capstone_backend.service.AdminUserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin/users")
class AdminUserController(
    private val adminUserService: AdminUserService
) {

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun list(): ResponseEntity<List<UserResponseDto>> =
        ResponseEntity.ok(adminUserService.listUsers())

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun create(@RequestBody req: CreateUserRequestDto): ResponseEntity<UserResponseDto> =
        ResponseEntity(adminUserService.createUser(req), HttpStatus.CREATED)
}
