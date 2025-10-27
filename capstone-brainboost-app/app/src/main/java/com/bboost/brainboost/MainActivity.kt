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

    // ViewBinding para acceder fácil a la UI
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

                        // 1. Guardar la sesión
                        sessionManager.saveAuth(token, role)

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

    private fun goToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        // Banderas para que el usuario no pueda volver al Login con el botón "atrás"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}
