package com.bboost.brainboost.network

import android.content.Context
import com.bboost.brainboost.dto.AiQuizDto
import com.bboost.brainboost.dto.AiSummaryDto
import okhttp3.OkHttpClient
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface BrainBoostApiService {
    @Multipart
    @POST("api/ai/pdf/resumen")
    suspend fun resumir(@Part file: MultipartBody.Part): AiSummaryDto

    @Multipart
    @POST("api/ai/pdf/quiz")
    suspend fun quiz(
        @Part file: MultipartBody.Part,
        @Query("numQuestions") numQuestions: Int = 5
    ): AiQuizDto
}

object ApiClient {
    private const val BASE_URL = "http://192.168.1.14:8080/"
    private var retrofit: Retrofit? = null

    fun initialize(context: Context) {
        val authInterceptor = AuthInterceptor(context)
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Servicio para IA (resumen y quiz sin persistir)
    val instance: BrainBoostApiService
        get() {
            if (retrofit == null) {
                throw IllegalStateException("ApiClient must be initialized first.")
            }
            return retrofit!!.create(BrainBoostApiService::class.java)
        }

    // Servicio para asignaturas, temas y persistencia
    val apiInstance: ApiService
        get() {
            if (retrofit == null) {
                throw IllegalStateException("ApiClient must be initialized first.")
            }
            return retrofit!!.create(ApiService::class.java)
        }
}