package com.bboost.brainboost

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.bboost.brainboost.dto.LoginRequestDto
import com.bboost.brainboost.network.RetrofitClient
import com.bboost.brainboost.ui.screens.LoginScreen
import com.bboost.brainboost.ui.theme.BrainBoostTheme
import com.bboost.brainboost.util.SessionManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        if (sessionManager.getToken() != null) {
            goToHome()
            return
        }

        setContent {
            BrainBoostTheme {
                LoginScreen(
                    onLogin = { email, password, onError, onSuccess ->
                        performLogin(email, password, onError, onSuccess)
                    }
                )
            }
        }
    }

    private fun performLogin(
        email: String,
        password: String,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.login(LoginRequestDto(email, password))

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val token = loginResponse?.token
                    val role = loginResponse?.role

                    if (token != null && role != null) {
                        sessionManager.saveAuth(token, role)

                        val bearer = "Bearer $token"
                        val meResp = RetrofitClient.instance.getMe(bearer)

                        if (meResp.isSuccessful) {
                            meResp.body()?.let {
                                sessionManager.saveUserId(it.id)
                            }
                        }

                        onSuccess()
                        goToHome()
                    } else {
                        onError("Respuesta de login incompleta")
                    }
                } else {
                    onError("Credenciales inválidas (${response.code()})")
                }

            } catch (e: Exception) {
                onError("Error de conexión: ${e.message}")
            }
        }
    }

    private fun goToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
