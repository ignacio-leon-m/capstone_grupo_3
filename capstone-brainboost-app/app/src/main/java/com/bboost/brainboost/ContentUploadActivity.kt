package com.bboost.brainboost

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bboost.brainboost.databinding.ActivityContentUploadBinding
import com.bboost.brainboost.network.ApiClient
import com.bboost.brainboost.util.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID

class ContentUploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContentUploadBinding
    private var selectedFileUri: Uri? = null
    private var currentTopicId: UUID? = null
    private var currentSubjectId: UUID? = null
    private var subjectName: String? = null
    private var topicName: String? = null
    private var actionType: String? = null

    // Launcher para el selector de archivos
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedFileUri = uri
            val fileName = getFileName(this, uri)
            binding.txtFileName.text = "Archivo: $fileName"
            binding.btnGenerateSummary.isEnabled = true
            binding.btnGenerateQuiz.isEnabled = true
            binding.btnSaveQuiz.isEnabled = true
            Log.d("ContentUpload", "✅ Archivo PDF seleccionado: $fileName")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContentUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("ContentUpload", "🚀 === INICIANDO CONTENT UPLOAD ACTIVITY ===")

        // Obtener datos del intent
        val topicIdString = intent.getStringExtra("TOPIC_ID")
        val subjectIdString = intent.getStringExtra("SUBJECT_ID")

        currentTopicId = try {
            UUID.fromString(topicIdString)
        } catch (e: Exception) {
            null
        }

        currentSubjectId = try {
            UUID.fromString(subjectIdString)
        } catch (e: Exception) {
            null
        }

        subjectName = intent.getStringExtra("SUBJECT_NAME")
        topicName = intent.getStringExtra("TOPIC_NAME")
        actionType = intent.getStringExtra("ACTION")

        // LOG: Mostrar datos recibidos del intent
        Log.d("ContentUpload", "📥 Datos recibidos del Intent:")
        Log.d("ContentUpload", "   - TOPIC_ID: '$topicIdString' -> UUID: $currentTopicId")
        Log.d("ContentUpload", "   - SUBJECT_ID: '$subjectIdString' -> UUID: $currentSubjectId")
        Log.d("ContentUpload", "   - SUBJECT_NAME: '$subjectName'")
        Log.d("ContentUpload", "   - TOPIC_NAME: '$topicName'")
        Log.d("ContentUpload", "   - ACTION: '$actionType'")

        // Mostrar información del tema seleccionado
        try {
            if (subjectName != null && topicName != null) {
                binding.txtSubjectInfo.text = "Asignatura: $subjectName - Tema: $topicName"
                Log.d("ContentUpload", "✅ UI actualizada con asignatura y tema")
            }
        } catch (e: Exception) {
            Log.w("ContentUpload", "⚠️ TextView txtSubjectInfo no encontrado en el layout")
        }

        setupListeners()
        setupActionButtons()

        Log.d("ContentUpload", "✅ === CONFIGURACIÓN COMPLETADA ===")
    }

    private fun setupListeners() {
        binding.btnSelectFile.setOnClickListener {
            Log.d("ContentUpload", "🖱️ Botón Seleccionar PDF clickeado")
            filePickerLauncher.launch("application/pdf")
        }

        binding.btnGenerateSummary.setOnClickListener {
            Log.d("ContentUpload", "🖱️ Botón Generar Resumen clickeado")
            callApi("summary")
        }

        binding.btnGenerateQuiz.setOnClickListener {
            Log.d("ContentUpload", "🖱️ Botón Generar Quiz clickeado")
            callApi("quiz")
        }

        binding.btnSaveQuiz.setOnClickListener {
            Log.d("ContentUpload", "🖱️ Botón Guardar Quiz en BD clickeado")
            callApi("quiz_persist")
        }
    }

    private fun setupActionButtons() {
        Log.d("ContentUpload", "⚙️ Configurando acciones automáticas. ActionType: '$actionType'")

        when (actionType) {
            "summary" -> {
                Log.d("ContentUpload", "🔁 Ejecutando acción automática: summary")
                binding.btnGenerateSummary.performClick()
            }
            "quiz" -> {
                Log.d("ContentUpload", "🔁 Ejecutando acción automática: quiz")
                binding.btnGenerateQuiz.performClick()
            }
            "quiz_persist" -> {
                Log.d("ContentUpload", "🔁 Ejecutando acción automática: quiz_persist")
                binding.btnSaveQuiz.performClick()
            }
            else -> {
                Log.d("ContentUpload", "ℹ️ No hay acción automática configurada")
            }
        }
    }

    private fun callApi(apiType: String) {
        Log.d("ContentUpload", "🌐 === INICIANDO LLAMADA API: $apiType ===")

        val uri = selectedFileUri
        if (uri == null) {
            Log.w("ContentUpload", "❌ No hay archivo seleccionado para la API call")
            Toast.makeText(this, "Por favor, selecciona un archivo primero", Toast.LENGTH_SHORT).show()
            return
        }

        val sessionManager = SessionManager(this)
        val token = sessionManager.getToken()
        val userRole = sessionManager.getRole()

        // VERIFICACIÓN DETALLADA DEL TOKEN
        Log.d("ContentUpload", "🔐 Verificación de autenticación:")
        Log.d("ContentUpload", "   - Token disponible: ${!token.isNullOrEmpty()}")
        Log.d("ContentUpload", "   - Longitud del token: ${token?.length ?: 0}")
        Log.d("ContentUpload", "   - Rol del usuario: '$userRole'")
        Log.d("ContentUpload", "   - Token (primeros 30 chars): '${token?.take(30)}...'")

        if (token.isNullOrEmpty()) {
            Log.e("ContentUpload", "❌ ERROR: Token JWT no disponible o vacío")
            Toast.makeText(this, "No estás autenticado. Por favor, inicia sesión nuevamente.", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("ContentUpload", "✅ Token JWT disponible, procediendo con API call")
        setLoading(true)

        lifecycleScope.launch(Dispatchers.IO) {
            var resultText: String

            try {
                Log.d("ContentUpload", "📦 Creando MultipartBody para el archivo")
                val filePart = createMultipartBody(this@ContentUploadActivity, uri)

                when (apiType) {
                    "summary" -> {
                        Log.d("ContentUpload", "📝 Llamando a API resumir")
                        val response = ApiClient.instance.resumir(filePart)
                        resultText = "Resumen:\n${response.summary}"
                        Log.d("ContentUpload", "✅ Resumen generado exitosamente")
                    }
                    "quiz" -> {
                        Log.d("ContentUpload", "❓ Llamando a API quiz")
                        val response = ApiClient.instance.quiz(filePart, 5)
                        resultText = "Quiz Generado (${response.questions.size} preguntas):\n\n" +
                                response.questions.joinToString("\n\n") { q ->
                                    val options = q.options.joinToString("\n  - ")
                                    "Pregunta: ${q.question}\n  - $options\nRespuesta: ${q.options[q.answerIndex]}"
                                }
                        Log.d("ContentUpload", "✅ Quiz generado exitosamente: ${response.questions.size} preguntas")
                    }
                    "quiz_persist" -> {
                        Log.d("ContentUpload", "💾 Preparando llamada a quizPersist")
                        Log.d("ContentUpload", "   - Validando IDs - SubjectId: $currentSubjectId, TopicId: $currentTopicId")

                        if (currentSubjectId == null || currentTopicId == null) {
                            resultText = "Error: No se ha seleccionado una asignatura y tema válidos. SubjectId: $currentSubjectId, TopicId: $currentTopicId"
                            Log.e("ContentUpload", "❌ IDs inválidos para quizPersist")
                        } else {
                            // LOG DETALLADO: Información que se enviará al backend
                            Log.d("ContentUpload", "📤 === ENVIANDO QUIZ PERSIST ===")
                            Log.d("ContentUpload", "   - SubjectId (asignaturaId): ${currentSubjectId!!}")
                            Log.d("ContentUpload", "   - SubjectId como String: '${currentSubjectId!!.toString()}'")
                            Log.d("ContentUpload", "   - TopicId: ${currentTopicId!!}")
                            Log.d("ContentUpload", "   - TopicName: '$topicName'")
                            Log.d("ContentUpload", "   - numQuestions: 5")
                            Log.d("ContentUpload", "   - UUID string length: ${currentSubjectId!!.toString().length}")

                            try {
                                Log.d("ContentUpload", "🔗 Realizando llamada a quizPersist con QUERY PARAMETERS")
                                val response = ApiClient.apiInstance.quizPersist(
                                    file = filePart,
                                    asignaturaId = currentSubjectId!!.toString(),
                                    tema = topicName ?: "Conceptos Básicos",
                                    numQuestions = 5
                                )

                                Log.d("ContentUpload", "📨 Respuesta recibida de quizPersist - Código: ${response.code()}, Éxito: ${response.isSuccessful}")

                                resultText = if (response.isSuccessful) {
                                    val responseBody = response.body()
                                    Log.d("ContentUpload", "✅ Quiz guardado exitosamente en BD: $responseBody")
                                    "✅ Quiz guardado exitosamente en la base de datos\n\n" +
                                            "Respuesta: $responseBody"
                                } else {
                                    val errorMessage = response.message()
                                    Log.e("ContentUpload", "❌ Error del servidor: $errorMessage, Código: ${response.code()}")
                                    "❌ Error guardando quiz: $errorMessage (Código: ${response.code()})"
                                }
                            } catch (e: Exception) {
                                Log.e("ContentUpload", "💥 Excepción durante quizPersist: ${e.message}", e)
                                resultText = "💥 Error en la llamada a la API: ${e.message}"
                            }
                        }
                    }
                    else -> {
                        resultText = "Tipo de acción no válido"
                        Log.e("ContentUpload", "❌ Tipo de API no reconocido: $apiType")
                    }
                }

            } catch (e: Exception) {
                Log.e("ContentUpload", "💥 Error general en la API: ${e.message}", e)
                resultText = "Error: ${e.message}"

                if (e.message?.contains("403") == true || e.message?.contains("Forbidden") == true) {
                    resultText = "❌ Error: Acceso denegado. Token expirado o inválido."
                    Log.e("ContentUpload", "🔐 Error de autenticación 403/Forbidden")

                    // Forzar re-login si el token está expirado
                    withContext(Dispatchers.Main) {
                        sessionManager.clearSession()
                        Toast.makeText(this@ContentUploadActivity, "Sesión expirada. Por favor, inicia sesión nuevamente.", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@ContentUploadActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    return@launch
                }
            }

            withContext(Dispatchers.Main) {
                setLoading(false)
                binding.txtApiResult.text = resultText
                Log.d("ContentUpload", "🏁 === LLAMADA API COMPLETADA: $apiType ===")
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSelectFile.isEnabled = !isLoading
        binding.btnGenerateSummary.isEnabled = !isLoading && selectedFileUri != null
        binding.btnGenerateQuiz.isEnabled = !isLoading && selectedFileUri != null
        binding.btnSaveQuiz.isEnabled = !isLoading && selectedFileUri != null

        if (isLoading) {
            Log.d("ContentUpload", "⏳ Mostrando loading...")
        } else {
            Log.d("ContentUpload", "✅ Ocultando loading...")
        }
    }

    // Helper para obtener el nombre del archivo desde una Uri
    private fun getFileName(context: Context, uri: Uri): String? {
        return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(nameIndex)
        }
    }

    // Helper para convertir la Uri del archivo en un MultipartBody.Part
    private fun createMultipartBody(context: Context, uri: Uri): MultipartBody.Part {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val mimeType = contentResolver.getType(uri) ?: "application/pdf"
        val fileName = getFileName(context, uri) ?: "documento.pdf"

        Log.d("ContentUpload", "📄 Creando MultipartBody - Archivo: '$fileName', MIME: '$mimeType'")

        val fileBytes = inputStream!!.readBytes()
        inputStream.close()

        val requestBody = fileBytes.toRequestBody(mimeType.toMediaTypeOrNull())

        Log.d("ContentUpload", "✅ MultipartBody creado - Tamaño: ${fileBytes.size} bytes")
        return MultipartBody.Part.createFormData("file", fileName, requestBody)
    }
}