package com.bboost.brainboost

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bboost.brainboost.databinding.ActivitySubjectSelectionBinding
import com.bboost.brainboost.dto.SubjectDto
import com.bboost.brainboost.dto.TopicDto
import com.bboost.brainboost.network.ApiClient
import com.bboost.brainboost.util.SessionManager
import kotlinx.coroutines.launch
import java.util.UUID

class SubjectSelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubjectSelectionBinding
    private lateinit var sessionManager: SessionManager
    private var subjects = listOf<SubjectDto>()
    private var topics = listOf<TopicDto>()
    private var selectedSubjectId: UUID? = null
    private var selectedTopicId: UUID? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubjectSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        setupUI()
        loadSubjects()
    }

    private fun setupUI() {
        binding.btnContinue.setOnClickListener {
            if (selectedTopicId != null && selectedSubjectId != null) { // Validamos ambos IDs
                val intent = Intent(this, ContentUploadActivity::class.java).apply {
                    putExtra("TOPIC_ID", selectedTopicId.toString())
                    putExtra("SUBJECT_ID", selectedSubjectId.toString()) // 💡 AGREGADO AQUÍ
                    putExtra("SUBJECT_NAME", getSelectedSubjectName())
                    putExtra("TOPIC_NAME", getSelectedTopicName())
                    // NOTA: No se pasa "ACTION" aquí, la ContentUploadActivity tendrá que
                    // determinar la acción por defecto o esperar más input del usuario.
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Por favor selecciona una asignatura y un tema", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadSubjects() {
        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE
                val userRole = sessionManager.getRole()
                val response = if (userRole == "admin") {
                    ApiClient.apiInstance.getAllSubjects()
                } else {
                    ApiClient.apiInstance.getMySubjects()
                }

                if (response.isSuccessful) {
                    subjects = response.body() ?: emptyList()
                    setupSubjectSpinner()
                } else {
                    Toast.makeText(this@SubjectSelectionActivity, "Error cargando asignaturas", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SubjectSelectionActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun setupSubjectSpinner() {
        val subjectNames = subjects.map { it.nombre }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            listOf("Selecciona una asignatura") + subjectNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSubject.adapter = adapter

        binding.spinnerSubject.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    selectedSubjectId = subjects[position - 1].id
                    loadTopics(selectedSubjectId!!)
                } else {
                    selectedSubjectId = null
                    clearTopics()
                }
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }
    }

    private fun loadTopics(subjectId: UUID) {
        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE
                val response = ApiClient.apiInstance.getTopicsBySubject(subjectId)
                if (response.isSuccessful) {
                    topics = response.body() ?: emptyList()
                    setupTopicSpinner()
                } else {
                    Toast.makeText(this@SubjectSelectionActivity, "Error cargando temas", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SubjectSelectionActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun setupTopicSpinner() {
        val topicNames = topics.map { it.nombre }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            listOf("Selecciona un tema") + topicNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTopic.adapter = adapter

        binding.spinnerTopic.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedTopicId = if (position > 0) topics[position - 1].id else null
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }
    }

    private fun clearTopics() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            listOf("Selecciona un tema"))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTopic.adapter = adapter
        selectedTopicId = null
    }

    private fun getSelectedSubjectName(): String {
        val position = binding.spinnerSubject.selectedItemPosition
        return if (position > 0) subjects[position - 1].nombre else ""
    }

    private fun getSelectedTopicName(): String {
        val position = binding.spinnerTopic.selectedItemPosition
        return if (position > 0) topics[position - 1].nombre else ""
    }
}