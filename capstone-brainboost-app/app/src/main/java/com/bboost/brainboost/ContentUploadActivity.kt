package com.bboost.brainboost

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bboost.brainboost.databinding.ActivityContentUploadBinding // <-- Importa tu ViewBinding
import com.bboost.brainboost.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ContentUploadActivity : AppCompatActivity() {

    // Ahora 'binding' encontrará las vistas del XML
    private lateinit var binding: ActivityContentUploadBinding

    private var selectedFileUri: Uri? = null

    // Launcher para el selector de archivos
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedFileUri = uri
            val fileName = getFileName(this, uri)
            binding.txtFileName.text = "Archivo: $fileName" // <- Ahora esto es válido
            binding.btnGenerateSummary.isEnabled = true     // <- Ahora esto es válido
            binding.btnGenerateQuiz.isEnabled = true      // <- Ahora esto es válido
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContentUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        // Botón para seleccionar el archivo
        binding.btnSelectFile.setOnClickListener { // <- Ahora esto es válido
            filePickerLauncher.launch("application/pdf")
        }

        // Botón para generar resumen
        binding.btnGenerateSummary.setOnClickListener { // <- Ahora esto es válido
            callApi("summary")
        }

        // Botón para generar quiz
        binding.btnGenerateQuiz.setOnClickListener { // <- Ahora esto es válido
            callApi("quiz")
        }
    }

    // --- Lógica para llamar a la API ---

    private fun callApi(apiType: String) {
        val uri = selectedFileUri
        if (uri == null) {
            Toast.makeText(this, "Por favor, selecciona un archivo primero", Toast.LENGTH_SHORT).show()
            return
        }

        // TODO: Reemplazar esto con tu lógica real de SharedPreferences o gestor de tokens
        val authToken = "Bearer TU_TOKEN_JWT_AQUI"

        setLoading(true) // Muestra un ProgressBar, deshabilita botones

        // Usamos el scope de la Activity para la corrutina
        lifecycleScope.launch(Dispatchers.IO) {
            var resultText: String

            try {
                // Preparamos el archivo para Multipart
                val filePart = createMultipartBody(this@ContentUploadActivity, uri)

                // Llamada de Red
                if (apiType == "summary") {
                    val response = ApiClient.instance.resumir(filePart)
                    resultText = "Resumen:\n${response.summary}"
                } else {
                    val response = ApiClient.instance.quiz(filePart, 5)
                    resultText = "Quiz Generado (${response.questions.size} preguntas):\n\n" +
                            response.questions.joinToString("\n\n") { q ->
                                val options = q.options.joinToString("\n  - ")
                                "Pregunta: ${q.question}\n  - $options\nRespuesta: ${q.options[q.answerIndex]}"
                            }
                }

            } catch (e: Exception) {
                Log.e("ContentUpload", "Error en la API: ${e.message}", e)
                resultText = "Error: ${e.message}"
            }

            // Actualizamos la UI en el hilo principal
            withContext(Dispatchers.Main) {
                setLoading(false)
                binding.txtApiResult.text = resultText // <- Ahora esto es válido
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE // <- Ahora esto es válido
        binding.btnSelectFile.isEnabled = !isLoading
        binding.btnGenerateSummary.isEnabled = !isLoading && selectedFileUri != null
        binding.btnGenerateQuiz.isEnabled = !isLoading && selectedFileUri != null
    }

    // --- Funciones de Utilidad (Helpers) ---

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

        val fileBytes = inputStream!!.readBytes()
        inputStream.close()

        val requestBody = fileBytes.toRequestBody(mimeType.toMediaTypeOrNull())

        // El "name" del Part debe coincidir con el @RequestParam("file") en Spring
        return MultipartBody.Part.createFormData("file", fileName, requestBody)
    }
}