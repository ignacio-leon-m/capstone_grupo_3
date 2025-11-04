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
import com.bboost.brainboost.databinding.ActivityAdminContentBinding
import com.bboost.brainboost.dto.SubjectDto
import com.bboost.brainboost.dto.TopicDto
import com.bboost.brainboost.network.ApiClient
import com.bboost.brainboost.util.SessionManager
import kotlinx.coroutines.launch
import java.util.UUID

class AdminContentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminContentBinding
    private lateinit var sessionManager: SessionManager
    private var subjects = listOf<SubjectDto>()
    private var topics = listOf<TopicDto>()
    private var selectedSubjectId: UUID? = null
    private var selectedTopicId: UUID? = null
    private var selectedFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        ApiClient.initialize(this) // Asegurando que la inicialización esté aquí si se movió

        // LOG para verificar que es admin
        val userRole = sessionManager.getRole()
        Log.d("AdminContent", "Usuario rol: $userRole")

        setupUI()
        loadSubjects()
    }

    private fun setupUI() {
        // Spinner listeners
        binding.spinnerSubject.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    selectedSubjectId = subjects[position - 1].id
                    Log.d("AdminContent", "Asignatura seleccionada: ${subjects[position - 1].nombre} - ID: $selectedSubjectId")
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
                    Log.d("AdminContent", "Tema seleccionado: ${topics[position - 1].nombre} - ID: $selectedTopicId")
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
            Log.d("AdminContent", "Seleccionando archivo PDF")
            selectPdfFile()
        }

        binding.btnCreateTopic.setOnClickListener {
            Log.d("AdminContent", "Creando nuevo tema")
            createNewTopic()
        }

        binding.btnGenerateSummary.setOnClickListener {
            Log.d("AdminContent", "Generando resumen")
            generateSummary()
        }

        binding.btnGenerateQuiz.setOnClickListener {
            Log.d("AdminContent", "Generando quiz")
            generateQuiz()
        }

        binding.btnSaveQuiz.setOnClickListener {
            Log.d("AdminContent", "Guardando quiz en BD")
            saveQuizToDatabase()
        }
    }

    private fun loadSubjects() {
        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE
                Log.d("AdminContent", "Cargando asignaturas...")
                val response = ApiClient.apiInstance.getAllSubjects()

                if (response.isSuccessful) {
                    subjects = response.body() ?: emptyList()
                    Log.d("AdminContent", "Asignaturas cargadas: ${subjects.size}")
                    setupSubjectSpinner()
                } else {
                    Log.e("AdminContent", "Error cargando asignaturas: ${response.code()}")
                    Toast.makeText(this@AdminContentActivity, "Error cargando asignaturas", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("AdminContent", "Error: ${e.message}", e)
                Toast.makeText(this@AdminContentActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun loadTopics(subjectId: UUID) {
        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE
                Log.d("AdminContent", "Cargando temas para asignatura: $subjectId")
                val response = ApiClient.apiInstance.getTopicsBySubject(subjectId)

                if (response.isSuccessful) {
                    topics = response.body() ?: emptyList()
                    Log.d("AdminContent", "Temas cargados: ${topics.size}")
                    setupTopicSpinner()
                } else {
                    Log.e("AdminContent", "Error cargando temas: ${response.code()}")
                    Toast.makeText(this@AdminContentActivity, "Error cargando temas", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("AdminContent", "Error: ${e.message}", e)
                Toast.makeText(this@AdminContentActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    // --- MÉTODOS CORREGIDOS ---
    private fun setupSubjectSpinner() {
        val subjectNames = subjects.map { it.nombre }
        // 🎯 CORRECCIÓN: Usar el layout personalizado (asumiendo que está en R.layout.simple_spinner_item_custom)
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item_custom,
            listOf("Selecciona una asignatura") + subjectNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSubject.adapter = adapter
        Log.d("AdminContent", "Spinner de asignaturas configurado con ${subjects.size} items")
    }

    private fun setupTopicSpinner() {
        val topicNames = topics.map { it.nombre }
        // 🎯 CORRECCIÓN: Usar el layout personalizado
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item_custom,
            listOf("Selecciona un tema") + topicNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTopic.adapter = adapter

        // Mostrar opción para crear nuevo tema
        binding.layoutCreateTopic.visibility = View.VISIBLE
        Log.d("AdminContent", "Spinner de temas configurado con ${topics.size} items")
    }
    // -------------------------

    private fun clearTopics() {
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item_custom, // Usar el layout personalizado aquí también
            listOf("Selecciona un tema"))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTopic.adapter = adapter
        binding.layoutCreateTopic.visibility = View.GONE
        updateUI()
        Log.d("AdminContent", "Temas limpiados")
    }

    private fun createNewTopic() {
        val topicName = binding.etNewTopic.text.toString().trim()
        if (topicName.isEmpty()) {
            Toast.makeText(this, "Ingresa un nombre para el tema", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedSubjectId == null) {
            Toast.makeText(this, "Selecciona una asignatura primero", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE
                Log.d("AdminContent", "Creando tema: '$topicName' para asignatura: $selectedSubjectId")
                val response = ApiClient.apiInstance.createTopic(
                    subjectId = selectedSubjectId.toString(),
                    topicName = topicName
                )

                if (response.isSuccessful) {
                    binding.etNewTopic.text.clear()
                    Log.d("AdminContent", "Tema creado exitosamente")
                    Toast.makeText(this@AdminContentActivity, "Tema creado exitosamente", Toast.LENGTH_SHORT).show()
                    loadTopics(selectedSubjectId!!) // Recargar temas
                } else {
                    Log.e("AdminContent", "Error creando tema: ${response.code()}")
                    Toast.makeText(this@AdminContentActivity, "Error creando tema", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("AdminContent", "Error: ${e.message}", e)
                Toast.makeText(this@AdminContentActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
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

        Log.d("AdminContent", "Navegando a ContentUploadActivity - Resumen")
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

        Log.d("AdminContent", "Navegando a ContentUploadActivity - Quiz")
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

        Log.d("AdminContent", "Navegando a ContentUploadActivity - Quiz Persist")
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
        Log.d("AdminContent", "Validación exitosa - Subject: $selectedSubjectId, Topic: $selectedTopicId")
        return true
    }

    private fun updateUI() {
        val hasSelection = selectedSubjectId != null && selectedTopicId != null
        binding.btnGenerateSummary.isEnabled = hasSelection
        binding.btnGenerateQuiz.isEnabled = hasSelection
        binding.btnSaveQuiz.isEnabled = hasSelection
        Log.d("AdminContent", "UI actualizada - Botones habilitados: $hasSelection")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PDF_PICKER_REQUEST && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                selectedFileUri = uri
                val fileName = getFileName(uri)
                binding.txtFileName.text = "Archivo seleccionado: $fileName"
                Log.d("AdminContent", "Archivo seleccionado: $fileName")
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
    }
}