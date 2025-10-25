package com.bboost.brainboost.network

import com.bboost.brainboost.dto.LoginRequestDto
import com.bboost.brainboost.dto.LoginResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<LoginResponseDto>
}