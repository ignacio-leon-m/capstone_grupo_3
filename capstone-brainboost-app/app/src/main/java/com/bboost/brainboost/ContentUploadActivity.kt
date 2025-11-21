package com.bboost.brainboost.ui.screens

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.bboost.brainboost.network.RetrofitClient
import com.bboost.brainboost.ui.theme.BrainBoostTheme
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

class ContentUploadActivity : ComponentActivity() {

    private lateinit var sessionManager: SessionManager

    private var selectedFileUri: Uri? = null
    private val TAG = "ContentUploadActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "🚀 ContentUploadActivity iniciada")

        sessionManager = SessionManager(this)

        val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            Log.d(TAG, "📄 Archivo seleccionado: $uri")
            selectedFileUri = uri
        }

        setContent {
            BrainBoostTheme {
                ContentUploadScreen(
                    fileName = selectedFileUri?.lastPathSegment ?: "Ningún archivo",
                    subjects = emptyList(),     // Esta pantalla YA NO maneja subjects
                    topics = emptyList(),       // Ni topics
                    selectedSubjectId = null,
                    selectedTopicId = null,
                    onSelectFile = {
                        launcher.launch("application/pdf")
                    },
                    onSelectSubject = {},
                    onSelectTopic = {},
                    onGenerate = { _, onResult, onError, onLoading ->
                        onError("❌ Esta pantalla no genera quizzes. Usa UploadQuestionsActivity.")
                    }
                )
            }
        }
    }
}
