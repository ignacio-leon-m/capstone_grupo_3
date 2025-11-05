
package org.duocuc.capstonebackend.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.duocuc.capstonebackend.dto.RegisterRequestDto
import org.duocuc.capstonebackend.dto.UserResponseDto
import org.duocuc.capstonebackend.model.Role
import org.duocuc.capstonebackend.service.AuthService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var authService: AuthService

    @Test
    @WithMockUser(authorities = ["admin"])
    fun `should register a new user when valid data is provided`() {
        // Given
        val registerRequest = RegisterRequestDto(
            name = "Test",
            lastName = "User",
            rut = "12345678-9",
            email = "test.user@example.com",
            phone = "123456789",
            password = "password",
            role = "alumno",
            degreeName = "Ingeniería en Informática"
        )

        val role = Role("alumno").apply { id = UUID.randomUUID() }
        val userResponse = UserResponseDto(
            id = UUID.randomUUID(),
            name = "Test",
            lastName = "User",
            rut = "12345678-9",
            role = role,
            email = "test.user@example.com",
            degreeName = "Ingeniería en Informática"
        )

        every { authService.userRegistry(any()) } returns userResponse

        // When & Then
        mockMvc.post("/api/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(registerRequest)
        }.andExpect {
            status { isCreated() }
            content {
                json(objectMapper.writeValueAsString(userResponse))
            }
        }
    }
}
