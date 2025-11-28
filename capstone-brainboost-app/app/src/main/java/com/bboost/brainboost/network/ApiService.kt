package com.bboost.brainboost.network

import com.bboost.brainboost.dto.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ===== AUTH =====
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<LoginResponseDto>

    @GET("api/auth/me")
    suspend fun getMe(
        @Header("Authorization") token: String
    ): Response<MeDto>

    // ===== FILES =====
    @Multipart
    @POST("api/files/upload-excel")
    suspend fun uploadStudents(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Response<String>

    // ===== AI PDF GENERAL =====
    @Multipart
    @POST("api/ai/pdf/query")
    suspend fun queryPdf(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("prompt") prompt: RequestBody
    ): Response<AiQueryResponseDto>

    // ===== SUBJECTS & TOPICS =====
    @GET("api/subjects/student/{studentId}")
    suspend fun getStudentSubjects(
        @Header("Authorization") token: String,
        @Path("studentId") studentId: String
    ): Response<List<SubjectInfoDto>>

    @GET("api/subjects/professor/{professorId}")
    suspend fun getProfessorSubjects(
        @Header("Authorization") token: String,
        @Path("professorId") professorId: String
    ): Response<List<SubjectInfoDto>>

    @GET("api/subjects/{subjectId}/topics")
    suspend fun getTopicsBySubject(
        @Header("Authorization") bearer: String,
        @Path("subjectId") subjectId: String
    ): Response<List<TopicDto>>

    // ===== HANGMAN =====
    @POST("api/hangman/start")
    suspend fun startHangman(
        @Header("Authorization") bearer: String,
        @Body body: HangmanGameStartDto
    ): Response<HangmanGameStateDto>

    @GET("api/hangman/games/{gameId}")
    suspend fun getHangmanState(
        @Header("Authorization") bearer: String,
        @Path("gameId") gameId: String
    ): Response<HangmanGameStateDto>

    @POST("api/hangman/games/{gameId}/attempt")
    suspend fun attemptLetter(
        @Header("Authorization") bearer: String,
        @Path("gameId") gameId: String,
        @Body body: HangmanAttemptDto
    ): Response<HangmanAttemptResponseDto>

    @POST("api/hangman/games/{gameId}/concepts/submit")
    suspend fun submitConcept(
        @Header("Authorization") bearer: String,
        @Path("gameId") gameId: String,
        @Body body: HangmanConceptSubmitDto
    ): Response<HangmanConceptResultDto>

    @POST("api/hangman/games/{gameId}/end")
    suspend fun endHangman(
        @Header("Authorization") bearer: String,
        @Path("gameId") gameId: String
    ): Response<HangmanGameResultDto>

    // ===== AI → GENERAR CONCEPTOS =====
    @Multipart
    @POST("api/ai/pdf/concepts")
    suspend fun uploadConceptsFromPdf(
        @Header("Authorization") bearer: String,
        @Part file: MultipartBody.Part,
        @Part("topicId") topicId: RequestBody
    ): Response<AiConceptsResponse>

    // ===== QUIZ → GUARDAR PREGUNTAS =====
    @POST("api/questions")
    suspend fun saveQuestions(
        @Header("Authorization") token: String,
        @Body questions: List<QuestionCreateDto>
    ): Response<Map<String, Int>>
    @Multipart
    @POST("api/ai/pdf/questions")
    suspend fun generateQuestions(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("subjectId") subjectId: RequestBody,
        @Part("topicId") topicId: RequestBody
    ): Response<AiQuestionsResponse>
    @GET("api/topics/{topicId}/questions")
    suspend fun getQuestionsByTopic(
        @Header("Authorization") token: String,
        @Path("topicId") topicId: String
    ): Response<List<Question>>

}