package com.bboost.brainboost

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
        val role = sessionManager.getRole()
        val userName = sessionManager.getUserName()

        Log.d("HomeActivity", "=== INFORMACIÓN DE USUARIO ===")
        Log.d("HomeActivity", "Nombre: ${userName ?: "NULL"}")
        Log.d("HomeActivity", "Rol: ${role ?: "NULL"}")
        Log.d("HomeActivity", "==============================")

        // ✅ ACTUALIZAR EL TEXTO DE BIENVENIDA
        if (!userName.isNullOrEmpty()) {
            binding.tvWelcome.text = "Bienvenido, $userName"
            Log.d("HomeActivity", "Texto actualizado: Bienvenido, $userName")
        } else {
            binding.tvWelcome.text = "Bienvenido, Profesor"
            Log.w("HomeActivity", "Nombre de usuario no encontrado, usando valor por defecto")
        }

        setupUI(role)
        setupClickListeners(role)
    }

    private fun setupUI(role: String?) {
        if (role == null) {
            Log.e("HomeActivity", "No se encontró rol, cerrando sesión")
            performLogout()
            return
        }

        // Ocultar todos los botones primero
        binding.btnUsers.visibility = View.GONE
        binding.btnUploadStudents.visibility = View.GONE
        binding.btnAdminContent.visibility = View.GONE
        binding.btnUploadContent.visibility = View.GONE
        binding.btnContent.visibility = View.GONE

        // Mostrar solo los botones según el rol
        when (role) {
            "admin" -> {
                Log.d("HomeActivity", "Mostrando botones de administrador")
                binding.btnUsers.visibility = View.VISIBLE
                binding.btnAdminContent.visibility = View.VISIBLE
                binding.btnContent.visibility = View.VISIBLE
            }
            "profesor" -> {
                Log.d("HomeActivity", "Mostrando botones de profesor")
                binding.btnUploadStudents.visibility = View.VISIBLE
                binding.btnUploadContent.visibility = View.VISIBLE
                binding.btnContent.visibility = View.VISIBLE
            }
            else -> {
                Log.w("HomeActivity", "Rol desconocido: $role")
                Toast.makeText(this, "Rol no reconocido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners(role: String?) {
        // Botón de Carga de Contenido (visible para ambos roles)
        binding.btnContent.setOnClickListener {
            Log.d("HomeActivity", "Botón Carga de Contenido clickeado")
            when (role) {
                "admin" -> {
                    Log.d("HomeActivity", "Admin → Navegando a AdminContentActivity")
                    startActivity(Intent(this, AdminContentActivity::class.java))
                }
                "profesor" -> {
                    Log.d("HomeActivity", "Profesor → Navegando a TeacherContentActivity")
                    startActivity(Intent(this, TeacherContentActivity::class.java))
                }
            }
        }

        // Botón específico de Subir Contenido (solo profesores)
        binding.btnUploadContent.setOnClickListener {
            Log.d("HomeActivity", "Botón Subir Contenido clickeado → TeacherContentActivity")
            startActivity(Intent(this, TeacherContentActivity::class.java))
        }

        // Botón de Administrar Contenido (solo admin)
        binding.btnAdminContent.setOnClickListener {
            Log.d("HomeActivity", "Botón Admin Content clickeado → AdminContentActivity")
            startActivity(Intent(this, AdminContentActivity::class.java))
        }

        // Botón de Carga de Estudiantes
        binding.btnUploadStudents.setOnClickListener {
            Log.d("HomeActivity", "Navegando a UploadStudentsActivity")
            startActivity(Intent(this, UploadStudentsActivity::class.java))
        }

        // Botón de Carga de Usuarios
        binding.btnUsers.setOnClickListener {
            Log.d("HomeActivity", "Botón Carga de Usuarios clickeado")
            Toast.makeText(this, "Gestión de usuarios - Próximamente", Toast.LENGTH_SHORT).show()
        }

        // Botón de Logout
        binding.btnLogout.setOnClickListener {
            performLogout()
        }
    }

    private fun performLogout() {
        Log.d("HomeActivity", "Cerrando sesión")
        sessionManager.clearSession()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
        super.onBackPressed()
    }
}