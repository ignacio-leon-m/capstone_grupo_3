package com.bboost.brainboost.network

import com.bboost.brainboost.dto.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import java.util.UUID

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<LoginResponseDto>


    @Multipart
    @POST("api/users/upload")
    suspend fun uploadStudents(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Response<String>

    // Resto de endpoints...
    @GET("api/subjects/my-subjects")
    suspend fun getMySubjects(): Response<List<SubjectDto>>

    @GET("api/subjects/all")
    suspend fun getAllSubjects(): Response<List<SubjectDto>>

    @GET("api/subjects/topics/{subjectId}")
    suspend fun getTopicsBySubject(@Path("subjectId") subjectId: UUID): Response<List<TopicDto>>

    @POST("api/subjects/topics-query")
    suspend fun createTopic(
        @Query("subjectId") subjectId: String,
        @Query("topicName") topicName: String
    ): Response<TopicDto>

    @Multipart
    @POST("api/ai/pdf/quiz/persist")
    suspend fun quizPersist(
        @Part file: MultipartBody.Part,
        @Query("asignaturaId") asignaturaId: String,
        @Query("tema") tema: String,
        @Query("numQuestions") numQuestions: Int
    ): Response<Map<String, Any>>
}