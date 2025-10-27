package com.bboost.brainboost.network

import com.bboost.brainboost.dto.LoginRequestDto
import com.bboost.brainboost.dto.LoginResponseDto
import com.bboost.brainboost.dto.RegisterRequestDto
import com.bboost.brainboost.dto.UserResponseDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<LoginResponseDto>

    @Multipart // 4. Indicar que es una subida multipart
    @POST("api/files/upload") // 5. La URL de tu controlador
    suspend fun uploadStudents(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Response<String>
}