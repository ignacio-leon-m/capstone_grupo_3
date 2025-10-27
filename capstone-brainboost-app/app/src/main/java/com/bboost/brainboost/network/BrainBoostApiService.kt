package com.bboost.brainboost.network

import com.bboost.brainboost.dto.AiQuizDto
import com.bboost.brainboost.dto.AiSummaryDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface BrainBoostApiService {
    // Replicando: @PostMapping("/resumen")
    @Multipart
    @POST("api/ai/pdf/resumen")
    suspend fun resumir(
        @Part file: MultipartBody.Part
        // Aquí puedes añadir @Header("Authorization") String token si es necesario
    ): AiSummaryDto

    // Replicando: @PostMapping("/quiz")
    @Multipart
    @POST("api/ai/pdf/quiz")
    suspend fun quiz(
        @Part file: MultipartBody.Part,
        @Query("numQuestions") numQuestions: Int = 5
        // Aquí puedes añadir @Header("Authorization") String token si es necesario
    ): AiQuizDto
}

// --- Cliente de Red (Singleton) ---

object ApiClient {
    // IMPORTANTE: Esta es la IP de tu notebook que configuramos
    private const val BASE_URL = "http://198.168.1.98:8080/"

    // Cliente con timeouts aumentados para subidas de archivos
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val instance: BrainBoostApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(BrainBoostApiService::class.java)
    }
}