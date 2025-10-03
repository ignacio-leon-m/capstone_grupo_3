package com.example.front.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.DELETE
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.example.front.data.remote.dto.*
import com.example.front.data.remote.dto.MateriaDto

// DTOs mínimos
data class LoginRequest(val email: String, val password: String)
data class RoleDto(val id: String?, val name: String)
data class UserDto(
    val id: String?,
    val name: String,
    val lastName: String,
    val email: String,
    val role: RoleDto
)

data class MateriaDto(
    val id: String,
    val name: String,
    val carrera: String
)
// DTOs adicionales (asegúrate de que existan en el paquete dto)
// data class TemaListItemDto(...)
// data class TemaDetailDto(...)
// data class QuizDto(...)

interface ApiService {
    // Endpoints existentes
    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<Any>

    @GET("api/users/me")
    suspend fun me(): Response<UserDto>

    @GET("api/materias")
    suspend fun materias(): Response<List<MateriaDto>>

    @POST("api/auth/logout")
    suspend fun logout(): Response<Unit>

    // ---

    // Nuevos endpoints integrados
    @GET("api/materias/{materiaId}/temas")
    suspend fun listarTemas(@Path("materiaId") materiaId: String): Response<List<TemaListItemDto>>

    @GET("api/temas/{temaId}")
    suspend fun temaDetalle(@Path("temaId") temaId: String): Response<TemaDetailDto>

    @GET("api/materias")
    suspend fun listarMaterias(): Response<List<com.example.front.data.remote.dto.MateriaDto>>

    @GET("api/temas/{temaId}/quiz")
    suspend fun temaQuiz(@Path("temaId") temaId: String): Response<QuizDto>

    // ---
    // Nuevo endpoint para subir documentos
    @Multipart
    @POST("api/profesores/materias/{materiaId}/temas")
    suspend fun crearTemaConDocumento(
        @Path("materiaId") materiaId: String,
        @Part("titulo") titulo: RequestBody,
        @Part("descripcion") descripcion: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<TemaDetailDto>


    @DELETE("api/profesores/temas/{temaId}/documentos/{docId}")
    suspend fun eliminarDocumento(
        @Path("temaId") temaId: String,
        @Path("docId") docId: String
    ): retrofit2.Response<Unit>

    @POST("api/profesores/temas/{temaId}/ai")
    suspend fun generarResumenYQuiz(@Path("temaId") temaId: String): retrofit2.Response<Unit>

    @DELETE("api/profesores/temas/{temaId}/ai")
    suspend fun borrarAi(@Path("temaId") temaId: String): retrofit2.Response<Unit>

    @DELETE("api/profesores/temas/{temaId}")
    suspend fun borrarTema(@Path("temaId") temaId: String): retrofit2.Response<Unit>


}