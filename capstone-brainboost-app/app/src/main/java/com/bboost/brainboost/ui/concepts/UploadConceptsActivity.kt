package com.bboost.brainboost.ui.concepts
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.bboost.brainboost.dto.AiConceptsResponse
import com.bboost.brainboost.network.RetrofitClient
import com.bboost.brainboost.ui.screens.UploadConceptsScreen
import com.bboost.brainboost.ui.screens.ConceptsResultScreen
import com.bboost.brainboost.ui.theme.BrainBoostTheme
import com.bboost.brainboost.util.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class UploadConceptsActivity : ComponentActivity() {

    companion object {
        const val EXTRA_TOPIC_ID = "EXTRA_TOPIC_ID"
        const val EXTRA_TOPIC_NAME = "EXTRA_TOPIC_NAME"
        const val EXTRA_SUBJECT_ID = "EXTRA_SUBJECT_ID"
        const val EXTRA_SUBJECT_NAME = "EXTRA_SUBJECT_NAME"
    }

    private lateinit var session: SessionManager
    private var selectedUri: Uri? = null
    private var fileName by mutableStateOf<String?>(null)

    private var topicId: UUID? = null
    private var topicName: String = "Tema"

    private var subjectId: UUID? = null
    private var subjectName: String = "Asignatura"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        session = SessionManager(this)

        topicName = intent.getStringExtra(EXTRA_TOPIC_NAME) ?: "Tema"
        subjectName = intent.getStringExtra(EXTRA_SUBJECT_NAME) ?: "Asignatura"

        topicId = intent.getStringExtra(EXTRA_TOPIC_ID)?.let { UUID.fromString(it) }
        subjectId = intent.getStringExtra(EXTRA_SUBJECT_ID)?.let { UUID.fromString(it) }

        if (topicId == null || subjectId == null) {
            Toast.makeText(this, "Error con IDs recibidos", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedUri = uri
            fileName = uri?.lastPathSegment ?: "archivo.pdf"
            Log.d("UploadConcepts", "📄 Archivo seleccionado: $fileName")
        }

        showUploadScreen(launcher)
    }

    private fun showUploadScreen(launcher: androidx.activity.result.ActivityResultLauncher<String>) {
        setContent {
            BrainBoostTheme {
                UploadConceptsScreen(
                    topicName = topicName,
                    subjectName = subjectName,
                    fileName = fileName,
                    onSelectFile = { launcher.launch("application/pdf") },
                    onSubmit = { onResult, onError, onLoading ->
                        val uri = selectedUri
                        val token = session.getToken()

                        if (uri == null) {
                            onError("Selecciona un archivo.")
                            return@UploadConceptsScreen
                        }
                        if (token.isNullOrBlank()) {
                            onError("Sesión inválida.")
                            return@UploadConceptsScreen
                        }

                        lifecycleScope.launch {
                            try {
                                onLoading(true)
                                val response = uploadConcepts(uri, token, topicId!!)
                                showResultsScreen(response)
                                onResult("¡Conceptos generados!")
                            } catch (e: Exception) {
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

    private suspend fun uploadConcepts(
        uri: Uri,
        token: String,
        topicId: UUID
    ): AiConceptsResponse {
        return withContext(Dispatchers.IO) {

            val inputStream = contentResolver.openInputStream(uri)
                ?: throw Exception("No se pudo leer archivo")

            val tempFile = File.createTempFile("concepts_", ".pdf", cacheDir)
            FileOutputStream(tempFile).use { inputStream.copyTo(it) }

            val filePart = MultipartBody.Part.createFormData(
                "file",
                tempFile.name,
                tempFile.readBytes().toRequestBody("application/pdf".toMediaTypeOrNull())
            )

            val topicPart = topicId.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())

            val resp = RetrofitClient.instance.uploadConceptsFromPdf(
                "Bearer $token",
                filePart,
                topicPart
            )

            if (!resp.isSuccessful) throw Exception("Error ${resp.code()}")

            resp.body() ?: throw Exception("Respuesta vacía del servidor")
        }
    }

    private fun showResultsScreen(response: AiConceptsResponse) {
        setContent {
            BrainBoostTheme {
                ConceptsResultScreen(
                    inserted = response.inserted,
                    examples = response.examples,
                    onFinish = { finish() }
                )
            }
        }
    }
}

