package com.bboost.brainboost.ui.subjects

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bboost.brainboost.databinding.ActivitySubjectListBinding
import com.bboost.brainboost.network.RetrofitClient
import com.bboost.brainboost.util.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubjectsListActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySubjectListBinding
    private lateinit var session: SessionManager
    private lateinit var adapter: SubjectAdapter

    private var mode: String = "quiz"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubjectListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        mode = intent.getStringExtra("MODE") ?: "quiz"

        adapter = SubjectAdapter(mutableListOf())

        binding.rvSubjects.layoutManager = LinearLayoutManager(this)
        binding.rvSubjects.adapter = adapter

        loadSubjects()
    }

    private fun loadSubjects() {
        val token = session.getToken()
        val role = session.getRole()
        val userId = session.getUserId()

        if (token.isNullOrBlank() || role.isNullOrBlank() || userId.isNullOrBlank()) {
            Toast.makeText(this, "Sesión inválida.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val bearer = "Bearer $token"

        binding.progressBarSubjects.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            val resp = when (role.lowercase()) {
                "alumno" -> RetrofitClient.instance.getStudentSubjects(bearer, userId)
                "profesor" -> RetrofitClient.instance.getProfessorSubjects(bearer, userId)
                else -> null
            }

            withContext(Dispatchers.Main) {
                binding.progressBarSubjects.visibility = View.GONE

                if (resp == null || !resp.isSuccessful) {
                    Toast.makeText(this@SubjectsListActivity, "Error cargando asignaturas", Toast.LENGTH_LONG).show()
                    return@withContext
                }

                val list = resp.body().orEmpty()
                adapter.setData(list)
            }
        }
    }
}

