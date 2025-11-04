package com.bboost.brainboost

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.bboost.brainboost.databinding.ActivityMainBinding
import com.bboost.brainboost.dto.LoginRequestDto
import com.bboost.brainboost.network.RetrofitClient
import com.bboost.brainboost.util.SessionManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Si ya hay un token, ir directo a Home sin mostrar el login
        if (sessionManager.getToken() != null) {
            goToHome()
        }

        // Asignar el clic del botón
        binding.btnLogin.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email y contraseña requeridos", Toast.LENGTH_SHORT).show()
            return
        }

        val loginRequest = LoginRequestDto(email, password)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.login(loginRequest)

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val token = loginResponse?.token
                    val role = loginResponse?.role

                    if (token != null && role != null) {
                        Log.i("LOGIN_SUCCESS", "Token: $token, Rol: $role")

                        // ✅ ACTUALIZADO: Guardar también el nombre del usuario
                        // Extraer el nombre del email (parte antes del @) como fallback
                        val userName = extractNameFromEmail(email)

                        sessionManager.saveAuth(token, role, userName)

                        // 2. Ir a Home
                        goToHome()

                    } else {
                        binding.tvResult.text = "Error: Respuesta vacía del servidor."
                    }

                } else {
                    val errorMsg = "Error: ${response.code()} - Credenciales incorrectas"
                    Log.e("LOGIN_ERROR", errorMsg)
                    binding.tvResult.text = errorMsg
                }
            } catch (e: Exception) {
                Log.e("LOGIN_EXCEPTION", "Excepción: ${e.message}", e)
                binding.tvResult.text = "Error de conexión: ${e.message}"
            }
        }
    }

    /**
     * Extrae un nombre amigable del email
     * Ejemplo: "juan.perez@gmail.com" -> "Juan Perez"
     */
    private fun extractNameFromEmail(email: String): String {
        return try {
            val namePart = email.substringBefore("@") // "juan.perez"
            namePart
                .replace(".", " ") // "juan perez"
                .split(" ")
                .joinToString(" ") { it ->
                    it.replaceFirstChar { char ->
                        if (char.isLowerCase()) char.titlecase() else char.toString()
                    }
                } // "Juan Perez"
        } catch (e: Exception) {
            // Si hay algún error, usar la parte antes del @ o un valor por defecto
            email.substringBefore("@").ifEmpty { "Usuario" }
        }
    }

    private fun goToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}