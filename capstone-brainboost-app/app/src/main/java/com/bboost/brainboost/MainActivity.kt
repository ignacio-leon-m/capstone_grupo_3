package com.bboost.brainboost

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.bboost.brainboost.databinding.ActivityMainBinding
import com.bboost.brainboost.dto.LoginRequestDto
import com.bboost.brainboost.network.RetrofitClient

class MainActivity : AppCompatActivity() {

    // ViewBinding para acceder fácil a la UI
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        // Creamos la petición
        val loginRequest = LoginRequestDto(email, password)

        // Usamos corutinas para la llamada de red
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.login(loginRequest)

                if (response.isSuccessful) {
                    // Éxito (Código 200 OK)
                    val loginResponse = response.body()
                    val token = loginResponse?.token
                    val role = loginResponse?.role

                    Log.i("LOGIN_SUCCESS", "Token: $token, Rol: $role")
                    binding.tvResult.text = "¡Login Exitoso! \nRol: $role"

                } else {
                    // Error (Ej: 401 Unauthorized, 404, etc.)
                    val errorMsg = "Error: ${response.code()} - Credenciales incorrectas"
                    Log.e("LOGIN_ERROR", errorMsg)
                    binding.tvResult.text = errorMsg
                }
            } catch (e: Exception) {
                // Error de red (sin conexión, URL mal, etc.)
                Log.e("LOGIN_EXCEPTION", "Excepción: ${e.message}", e)
                binding.tvResult.text = "Error de conexión: ${e.message}"
            }
        }
    }
}
