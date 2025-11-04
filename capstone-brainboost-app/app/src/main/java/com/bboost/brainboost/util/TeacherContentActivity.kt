package com.bboost.brainboost

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bboost.brainboost.databinding.ActivityTeacherContentBinding
import com.bboost.brainboost.dto.SubjectDto
import com.bboost.brainboost.dto.TopicDto
import com.bboost.brainboost.network.ApiClient // ← USA EL MISMO QUE EL ADMIN
import com.bboost.brainboost.util.SessionManager
import kotlinx.coroutines.launch
import java.util.UUID

class TeacherContentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherContentBinding
    private lateinit var sessionManager: SessionManager
    private var subjects = listOf<SubjectDto>()
    private var topics = listOf<TopicDto>()
    private var selectedSubjectId: UUID? = null
    private var selectedTopicId: UUID? = null
    private var selectedFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // IMPORTANTE: Asegurarse que ApiClient está inicializado
        ApiClient.initialize(this)

        // LOG para verificar que es profesor
        val userRole = sessionManager.getRole()
        val token = sessionManager.getToken()
        Log.d("TeacherContent", "Usuario rol: $userRole")
        Log.d("TeacherContent", "Token presente: ${!token.isNullOrEmpty()}")

        setupUI()
        loadSubjects()
    }

    private fun setupUI() {
        // Spinner listeners - EXACTAMENTE IGUAL QUE EL ADMIN
        binding.spinnerSubject.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    selectedSubjectId = subjects[position - 1].id
                    Log.d("TeacherContent", "Asignatura seleccionada: ${subjects[position - 1].nombre} - ID: $selectedSubjectId")
                    loadTopics(selectedSubjectId!!)
                } else {
                    selectedSubjectId = null
                    clearTopics()
                }
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }

        binding.spinnerTopic.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    selectedTopicId = topics[position - 1].id
                    Log.d("TeacherContent", "Tema seleccionado: ${topics[position - 1].nombre} - ID: $selectedTopicId")
                    updateUI()
                } else {
                    selectedTopicId = null
                    updateUI()
                }
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }

        // Button listeners
        binding.btnSelectFile.setOnClickListener {
            Log.d("TeacherContent", "Seleccionando archivo PDF")
            selectPdfFile()
        }

        binding.btnGenerateSummary.setOnClickListener {
            Log.d("TeacherContent", "Generando resumen")
            generateSummary()
        }

        binding.btnGenerateQuiz.setOnClickListener {
            Log.d("TeacherContent", "Generando quiz")
            generateQuiz()
        }

        binding.btnSaveQuiz.setOnClickListener {
            Log.d("TeacherContent", "Guardando quiz en BD")
            saveQuizToDatabase()
        }
    }

    private fun loadSubjects() {
        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE
                Log.d("TeacherContent", "Cargando asignaturas...")

                // ⚡ USA EXACTAMENTE LA MISMA LÓGICA QUE EL ADMIN
                val response = ApiClient.apiInstance.getMySubjects()

                if (response.isSuccessful) {
                    subjects = response.body() ?: emptyList()
                    Log.d("TeacherContent", "Asignaturas cargadas: ${subjects.size}")
                    setupSubjectSpinner()

                    if (subjects.isEmpty()) {
                        Toast.makeText(this@TeacherContentActivity, "No hay asignaturas disponibles", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("TeacherContent", "Error cargando asignaturas: ${response.code()} - ${response.message()}")
                    handleErrorResponse(response.code())
                }
            } catch (e: Exception) {
                Log.e("TeacherContent", "Error: ${e.message}", e)
                Toast.makeText(this@TeacherContentActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun loadTopics(subjectId: UUID) {
        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE
                Log.d("TeacherContent", "Cargando temas para asignatura: $subjectId")

                // ⚡ USA EXACTAMENTE LA MISMA LÓGICA QUE EL ADMIN
                val response = ApiClient.apiInstance.getTopicsBySubject(subjectId)

                if (response.isSuccessful) {
                    topics = response.body() ?: emptyList()
                    Log.d("TeacherContent", "Temas cargados: ${topics.size}")
                    setupTopicSpinner()
                } else {
                    Log.e("TeacherContent", "Error cargando temas: ${response.code()} - ${response.message()}")
                    Toast.makeText(this@TeacherContentActivity, "Error cargando temas", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("TeacherContent", "Error: ${e.message}", e)
                Toast.makeText(this@TeacherContentActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun handleErrorResponse(errorCode: Int) {
        when (errorCode) {
            403 -> {
                Toast.makeText(this, "Acceso denegado. No tienes permisos para acceder a las asignaturas.", Toast.LENGTH_LONG).show()
                Log.e("TeacherContent", "Error 403 - El profesor no tiene permisos para ver asignaturas")
            }
            401 -> {
                Toast.makeText(this, "Sesión expirada. Por favor, inicie sesión nuevamente.", Toast.LENGTH_LONG).show()
                sessionManager.clearSession()
                finish()
            }
            else -> {
                Toast.makeText(this, "Error del servidor: $errorCode", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSubjectSpinner() {
        val subjectNames = subjects.map { it.nombre }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            listOf("Selecciona una asignatura") + subjectNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSubject.adapter = adapter
        Log.d("TeacherContent", "Spinner de asignaturas configurado con ${subjects.size} items")
    }

    private fun setupTopicSpinner() {
        val topicNames = topics.map { it.nombre }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            listOf("Selecciona un tema") + topicNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTopic.adapter = adapter
        updateUI()
        Log.d("TeacherContent", "Spinner de temas configurado con ${topics.size} items")
    }

    private fun clearTopics() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            listOf("Selecciona un tema"))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTopic.adapter = adapter
        updateUI()
        Log.d("TeacherContent", "Temas limpiados")
    }

    private fun selectPdfFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, PDF_PICKER_REQUEST)
    }

    private fun generateSummary() {
        if (!validateSelection()) return

        Log.d("TeacherContent", "Navegando a ContentUploadActivity - Resumen")
        val intent = Intent(this, ContentUploadActivity::class.java).apply {
            putExtra("TOPIC_ID", selectedTopicId.toString())
            putExtra("SUBJECT_ID", selectedSubjectId.toString())
            putExtra("SUBJECT_NAME", binding.spinnerSubject.selectedItem.toString())
            putExtra("TOPIC_NAME", binding.spinnerTopic.selectedItem.toString())
            putExtra("ACTION", "summary")
        }
        startActivity(intent)
    }

    private fun generateQuiz() {
        if (!validateSelection()) return

        Log.d("TeacherContent", "Navegando a ContentUploadActivity - Quiz")
        val intent = Intent(this, ContentUploadActivity::class.java).apply {
            putExtra("TOPIC_ID", selectedTopicId.toString())
            putExtra("SUBJECT_ID", selectedSubjectId.toString())
            putExtra("SUBJECT_NAME", binding.spinnerSubject.selectedItem.toString())
            putExtra("TOPIC_NAME", binding.spinnerTopic.selectedItem.toString())
            putExtra("ACTION", "quiz")
        }
        startActivity(intent)
    }

    private fun saveQuizToDatabase() {
        if (!validateSelection()) return

        Log.d("TeacherContent", "Navegando a ContentUploadActivity - Quiz Persist")
        val intent = Intent(this, ContentUploadActivity::class.java).apply {
            putExtra("TOPIC_ID", selectedTopicId.toString())
            putExtra("SUBJECT_ID", selectedSubjectId.toString())
            putExtra("SUBJECT_NAME", binding.spinnerSubject.selectedItem.toString())
            putExtra("TOPIC_NAME", binding.spinnerTopic.selectedItem.toString())
            putExtra("ACTION", "quiz_persist")
        }
        startActivity(intent)
    }

    private fun validateSelection(): Boolean {
        if (selectedSubjectId == null) {
            Toast.makeText(this, "Selecciona una asignatura", Toast.LENGTH_SHORT).show()
            return false
        }
        if (selectedTopicId == null) {
            Toast.makeText(this, "Selecciona un tema", Toast.LENGTH_SHORT).show()
            return false
        }
        Log.d("TeacherContent", "Validación exitosa - Subject: $selectedSubjectId, Topic: $selectedTopicId")
        return true
    }

    private fun updateUI() {
        val hasSelection = selectedSubjectId != null && selectedTopicId != null
        binding.btnGenerateSummary.isEnabled = hasSelection
        binding.btnGenerateQuiz.isEnabled = hasSelection
        binding.btnSaveQuiz.isEnabled = hasSelection
        Log.d("TeacherContent", "UI actualizada - Botones habilitados: $hasSelection")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PDF_PICKER_REQUEST && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                selectedFileUri = uri
                val fileName = getFileName(uri)
                binding.txtFileName.text = "Archivo seleccionado: $fileName"
                Log.d("TeacherContent", "Archivo seleccionado: $fileName")
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        var fileName = "documento.pdf"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            fileName = cursor.getString(nameIndex) ?: "documento.pdf"
        }
        return fileName
    }

    companion object {
        private const val PDF_PICKER_REQUEST = 1001

        fun start(context: Context) {
            val intent = Intent(context, TeacherContentActivity::class.java)
            context.startActivity(intent)
        }
    }
}