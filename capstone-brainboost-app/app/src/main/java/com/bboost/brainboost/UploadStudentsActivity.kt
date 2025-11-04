package com.bboost.brainboost

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedFileUri = uri
            val fileName = getFileName(uri)
            binding.tvFileName.text = fileName
            binding.btnUpload.isEnabled = true
            binding.tvResult.isVisible = false
            Log.d("FILE_SELECTED", "Archivo seleccionado: $fileName")
        } else {
            Log.d("FilePicker", "No file selected")
            binding.btnUpload.isEnabled = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadStudentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        setupUI()
        setupClickListeners()

        // Debug: Verificar datos de sesión
        logSessionData()
    }

    private fun setupUI() {
        binding.tvResult.isVisible = false
        binding.btnUpload.isEnabled = false
    }

    private fun setupClickListeners() {
        binding.btnSelectFile.setOnClickListener {
            selectFile()
        }

        binding.btnUpload.setOnClickListener {
            performUpload()
        }
    }

    private fun logSessionData() {
        val token = sessionManager.getToken()
        val role = sessionManager.getRole()
        Log.d("SESSION_DEBUG", "Token: ${token?.take(10)}...")
        Log.d("SESSION_DEBUG", "Role: $role")
        Log.d("SESSION_DEBUG", "Token length: ${token?.length}")
    }

    private fun selectFile() {
        filePickerLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    }

    private fun performUpload() {
        // Verificar archivo seleccionado
        val uri = selectedFileUri
        if (uri == null) {
            showError("Por favor, selecciona un archivo primero")
            return
        }

        // Verificar token
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            showError("Error de sesión. Por favor, inicia sesión nuevamente.")
            redirectToLogin()
            return
        }

        // Verificar rol de usuario
        val userRole = sessionManager.getRole()
        if (userRole != "profesor") {
            showError("Error: Solo los profesores pueden cargar alumnos. Rol actual: $userRole")
            return
        }

        val authToken = "Bearer $token"
        Log.d("UPLOAD_DEBUG", "Iniciando upload con token: ${token.take(10)}...")
        Log.d("UPLOAD_DEBUG", "Rol de usuario: $userRole")

        // Mostrar estado de carga
        setUploadState(true)

        lifecycleScope.launch {
            try {
                // Leer archivo
                val inputStream = contentResolver.openInputStream(uri)
                val fileBytes = inputStream!!.readBytes()
                inputStream.close()

                Log.d("UPLOAD_DEBUG", "Tamaño del archivo: ${fileBytes.size} bytes")

                // Crear Multipart
                val requestBody = fileBytes.toRequestBody(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".toMediaTypeOrNull()
                )

                val filePart = MultipartBody.Part.createFormData(
                    "file",
                    getFileName(uri),
                    requestBody
                )

                // Llamar API
                Log.d("UPLOAD_DEBUG", "Enviando request a la API...")
                val response = RetrofitClient.instance.uploadStudents(authToken, filePart)

                if (response.isSuccessful) {
                    val successMsg = response.body() ?: "Archivo procesado exitosamente"
                    Log.i("UPLOAD_SUCCESS", successMsg)
                    showSuccess(successMsg)
                    resetFileSelection()
                } else {
                    val errorCode = response.code()
                    val errorMsg = response.errorBody()?.string() ?: "Error desconocido ($errorCode)"
                    Log.e("UPLOAD_ERROR", "Code: $errorCode, Msg: $errorMsg")

                    when (errorCode) {
                        403 -> showError("Acceso denegado. Verifica que tengas permisos de profesor.")
                        401 -> {
                            showError("Sesión expirada. Por favor, inicia sesión nuevamente.")
                            redirectToLogin()
                        }
                        else -> showError("Error $errorCode: $errorMsg")
                    }
                }

            } catch (e: Exception) {
                Log.e("UPLOAD_EXCEPTION", "Excepción: ${e.message}", e)
                showError("Error de conexión: ${e.message}")
            } finally {
                setUploadState(false)
            }
        }
    }

    private fun setUploadState(uploading: Boolean) {
        binding.btnUpload.isEnabled = !uploading && selectedFileUri != null
        binding.btnUpload.text = if (uploading) "Subiendo..." else "Cargar"
        binding.btnSelectFile.isEnabled = !uploading
    }

    private fun showError(message: String) {
        binding.tvResult.text = message
        binding.tvResult.setTextColor(ContextCompat.getColor(this, R.color.brainboost_error))
        binding.tvResult.isVisible = true
        Log.e("UI_ERROR", message)
    }

    private fun showSuccess(message: String) {
        binding.tvResult.text = message
        binding.tvResult.setTextColor(ContextCompat.getColor(this, R.color.brainboost_success))
        binding.tvResult.isVisible = true
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun resetFileSelection() {
        selectedFileUri = null
        binding.tvFileName.text = "Ningún archivo seleccionado"
        binding.btnUpload.isEnabled = false
    }

    private fun redirectToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

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