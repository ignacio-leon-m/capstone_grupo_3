package com.bboost.brainboost.ui.concepts

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bboost.brainboost.R
import com.bboost.brainboost.dto.ConceptCreateDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UploadConceptsResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_concepts_result)

        Log.e("UploadConceptsResult", "=== onCreate INICIADO ===")
        Log.e("UploadConceptsResult", "Intent: $intent")
        Log.e("UploadConceptsResult", "Extras: ${intent?.extras}")

        // ====================================
        //   SEGURA: LECTURA DE EXTRAS
        // ====================================
        if (intent == null) {
            Log.e("UploadConceptsResult", "ERROR: Intent es null")
            Toast.makeText(this, "Error: No se recibieron datos", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val extras = intent.extras
        if (extras == null) {
            Log.e("UploadConceptsResult", "ERROR: Extras es null")
            Toast.makeText(this, "Error: No se recibieron datos", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val inserted = extras.getInt("inserted", 0)
        val json = extras.getString("examples_json") ?: "[]"

        Log.e("UploadConceptsResult", "Datos recibidos - inserted: $inserted, json: $json")

        val listType = object : TypeToken<List<ConceptCreateDto>>() {}.type
        val examples: List<ConceptCreateDto> = try {
            Gson().fromJson(json, listType)
        } catch (e: Exception) {
            Log.e("UploadConceptsResult", "ERROR parsing JSON: ${e.message}")
            emptyList()
        }

        val tvResult: TextView = findViewById(R.id.tvInsertSummary)
        val tvExamples: TextView = findViewById(R.id.tvExamples)

        tvResult.text = "Conceptos insertados: $inserted\nEjemplos:\n"
        tvExamples.text = examples.joinToString("\n") { "• ${it.word}: ${it.hint}" }

        Log.e("UploadConceptsResult", "=== onCreate COMPLETADO ===")
    }
}