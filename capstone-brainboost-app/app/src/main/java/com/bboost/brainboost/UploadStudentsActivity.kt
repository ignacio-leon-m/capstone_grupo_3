package com.bboost.brainboost

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bboost.brainboost.databinding.ActivityUploadStudentsBinding
import com.bboost.brainboost.network.RetrofitClient
import com.bboost.brainboost.util.SessionManager
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody


class UploadStudentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadStudentsBinding
    private lateinit var sessionManager: SessionManager
    private var selectedFileUri: Uri? = null

    // 1. Preparamos el lanzador del selector de archivos
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            // Archivo seleccionado
            selectedFileUri = uri
            val fileName = getFileName(uri)
            binding.tvFileName.text = fileName
            binding.btnUpload.isEnabled = true
            binding.tvResult.isVisible = false
        } else {
            // Usuario canceló
            Log.d("FilePicker", "No file selected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadStudentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        binding.tvResult.isVisible = false

        // 2. Clic para seleccionar archivo
        binding.btnSelectFile.setOnClickListener {
            selectFile()
        }

        // 3. Clic para subir archivo
        binding.btnUpload.setOnClickListener {
            performUpload()
        }
    }

    private fun selectFile() {
        // Lanzamos el Intent para buscar archivos Excel
        filePickerLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    }

    private fun performUpload() {
        val uri = selectedFileUri
        if (uri == null) {
            Toast.makeText(this, "Por favor, selecciona un archivo primero", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener token
        val token = sessionManager.getToken()
        if (token == null) {
            Toast.makeText(this, "Error de sesión", Toast.LENGTH_SHORT).show()
            return
        }
        val authToken = "Bearer $token"

        lifecycleScope.launch {
            try {
                // 4. Convertir el archivo (Uri) en una parte Multipart
                // Leemos los bytes del archivo
                val inputStream = contentResolver.openInputStream(uri)
                val fileBytes = inputStream!!.readBytes()
                inputStream.close()

                // Creamos el RequestBody
                val requestBody = fileBytes.toRequestBody(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".toMediaTypeOrNull()
                )

                // Creamos el MultipartBody.Part
                // El "file" DEBE coincidir con el @RequestParam("file") del backend
                val filePart = MultipartBody.Part.createFormData(
                    "file",
                    getFileName(uri), // El nombre del archivo
                    requestBody
                )

                // 5. Llamar a la API
                val response = RetrofitClient.instance.uploadStudents(authToken, filePart)

                if (response.isSuccessful) {
                    // Éxito
                    val successMsg = response.body() ?: "Subida exitosa"
                    Log.i("UPLOAD_SUCCESS", successMsg)
                    Toast.makeText(this@UploadStudentsActivity, successMsg, Toast.LENGTH_LONG).show()
                    binding.tvResult.isVisible = false
                } else {
                    // Error (400, 403, 500...)
                    val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                    Log.e("UPLOAD_ERROR", "Code: ${response.code()}, Msg: $errorMsg")
                    binding.tvResult.text = errorMsg
                    binding.tvResult.isVisible = true
                }

            } catch (e: Exception) {
                // Error de red
                Log.e("UPLOAD_EXCEPTION", "Excepción: ${e.message}", e)
                binding.tvResult.text = "Error de conexión: ${e.message}"
                binding.tvResult.isVisible = true
            }
        }
    }

    // Helper para obtener el nombre del archivo desde la Uri
    private fun getFileName(uri: Uri): String {
        var name = "unknown.xlsx"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    name = it.getString(nameIndex)
                }
            }
        }
        return name
    }
}