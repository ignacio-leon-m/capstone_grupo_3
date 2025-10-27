package com.bboost.brainboost

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bboost.brainboost.databinding.ActivityHomeBinding
import com.bboost.brainboost.util.SessionManager

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var sessionManager: SessionManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // 1. Obtener el rol guardado
        val role = sessionManager.getRole()

        // 2. Configurar la UI según el rol
        setupUI(role)

        // 3. Configurar el botón de logout
        binding.btnLogout.setOnClickListener {
            performLogout()
        }

        binding.btnContent.setOnClickListener {
            Toast.makeText(this, "Ir a Carga de Contenido", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ContentUploadActivity::class.java)
            startActivity(intent)
        }
        binding.btnUploadStudents.setOnClickListener {
            val intent = Intent(this, UploadStudentsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupUI(role: String?) {
        if (role == null) {
            // Si por alguna razón no hay rol, volver al login
            performLogout()
            return
        }

        // El botón "Carga de Contenido" (btnContent) es visible para todos por defecto.

        // Lógica de visibilidad basada en tu home.html
        when (role) {
            "admin" -> {
                binding.btnUsers.visibility = View.VISIBLE
            }
            "profesor" -> {
                binding.btnUploadStudents.visibility = View.VISIBLE
            }
            // "alumno" u otros roles no ven botones extra
        }
    }

    private fun performLogout() {
        // 1. Borrar la sesión
        sessionManager.clearSession()

        // 2. Navegar de vuelta a MainActivity (Login)
        val intent = Intent(this, MainActivity::class.java)

        // Banderas para limpiar el historial de navegación:
        // No puedes volver a "Home" con el botón "atrás"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}