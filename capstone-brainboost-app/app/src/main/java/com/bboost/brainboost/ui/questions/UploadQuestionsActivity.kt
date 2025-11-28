package com.bboost.brainboost.ui.questions

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.bboost.brainboost.dto.AiQuestionsResponse
import com.bboost.brainboost.ui.screens.UploadQuestionsScreen
import com.bboost.brainboost.ui.screens.QuizEditScreen
import com.bboost.brainboost.ui.theme.BrainBoostTheme
import com.bboost.brainboost.network.RetrofitClient
import com.bboost.brainboost.util.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class UploadQuestionsActivity : ComponentActivity() {

    companion object {
        const val EXTRA_TOPIC_ID = "EXTRA_TOPIC_ID"
        const val EXTRA_TOPIC_NAME = "EXTRA_TOPIC_NAME"
        const val EXTRA_SUBJECT_ID = "EXTRA_SUBJECT_ID"
        const val EXTRA_SUBJECT_NAME = "EXTRA_SUBJECT_NAME"
    }

    private lateinit var sessionManager: SessionManager

    private var selectedUri by mutableStateOf<Uri?>(null)

    // *** ESTADO OBSERVABLE PARA COMPOSE ***
    private var fileName by mutableStateOf<String?>(null)

    private var topicId: UUID? = null
    private var topicName: String = "Tema"

    private var subjectId: UUID? = null
    private var subjectName: String = "Asignatura"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("UploadQuestions", "🔄 Activity creada")

        sessionManager = SessionManager(this)

        val extras = intent.extras
        topicName = extras?.getString(EXTRA_TOPIC_NAME) ?: "Tema"
        subjectName = extras?.getString(EXTRA_SUBJECT_NAME) ?: "Asignatura"

        topicId = extras?.getString(EXTRA_TOPIC_ID)?.let { UUID.fromString(it) }
        subjectId = extras?.getString(EXTRA_SUBJECT_ID)?.let { UUID.fromString(it) }

        if (topicId == null || subjectId == null) {
            Toast.makeText(this, "Error con IDs del tema/asignatura", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // --- SELECTOR REAL DE PDF ---
        val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedUri = uri
            fileName = uri?.lastPathSegment ?: "archivo.pdf"

            Log.d("UploadQuestions", "📄 Archivo seleccionado: $uri (nombre = $fileName)")
        }

        setContent {
            BrainBoostTheme {
                UploadQuestionsScreen(
                    topicName = topicName,
                    subjectName = subjectName,
                    fileName = fileName,             // ahora SÍ se refleja
                    onSelectFile = {
                        Log.d("UploadQuestions", "📄 Abriendo selector de archivos...")
                        launcher.launch("application/pdf")
                    },
                    onSubmit = { onResult, onError, onLoading ->
                        val uri = selectedUri
                        val token = sessionManager.getToken()

                        if (uri == null) {
                            onError("Selecciona un archivo primero.")
                            return@UploadQuestionsScreen
                        }
                        if (token.isNullOrBlank()) {
                            onError("Error de sesión.")
                            return@UploadQuestionsScreen
                        }

                        lifecycleScope.launch {
                            try {
                                onLoading(true)
                                val response = uploadAndGenerateQuestions(
                                    uri, subjectId!!, topicId!!, token
                                )

                                Log.d("UploadQuestions", "✅ Generó ${response.saved} preguntas")

                                navigateToEditScreen(response)

                                onResult("¡Preguntas generadas!")

                            } catch (e: Exception) {
                                Log.e("UploadQuestions", "❌ Error generando preguntas", e)
                                onError("Error: ${e.message}")
                            } finally {
                                onLoading(false)
                            }
                        }
                    }
                )
            }
        }
    }

    private fun navigateToEditScreen(questionsResponse: AiQuestionsResponse) {
        setContent {
            BrainBoostTheme {
                QuizEditScreen(
                    response = questionsResponse,
                    subjectId = subjectId!!,
                    topicId = topicId!!,
                    onFinish = { finish() }
                )
            }
        }
    }

    private suspend fun uploadAndGenerateQuestions(
        uri: Uri,
        subjectId: UUID,
        topicId: UUID,
        token: String
    ): AiQuestionsResponse {

        return withContext(Dispatchers.IO) {

            Log.d("UploadQuestions", "📁 Procesando PDF…")

            val inputStream = contentResolver.openInputStream(uri)
                ?: throw Exception("No se pudo leer el archivo")

            val tempFile = File.createTempFile("upload_questions", ".pdf", cacheDir)
            FileOutputStream(tempFile).use { output ->
                inputStream.copyTo(output)
            }

            val filePart = MultipartBody.Part.createFormData(
                "file",
                tempFile.name,
                tempFile.asRequestBody("application/pdf".toMediaTypeOrNull())
            )

            val subjectPart = subjectId.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())

            val topicPart = topicId.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())

            Log.d("UploadQuestions", "🚀 Enviando request...")

            val response = RetrofitClient.instance.generateQuestions(
                "Bearer $token",
                filePart,
                subjectPart,
                topicPart
            )

            if (!response.isSuccessful) {
                throw Exception("Error del servidor: ${response.code()}")
            }

            response.body() ?: throw Exception("Respuesta vacía del servidor")
        }
    }
}
