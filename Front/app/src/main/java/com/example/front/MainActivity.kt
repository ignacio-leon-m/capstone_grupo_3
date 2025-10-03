package com.example.front


import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.front.data.remote.ApiClient
import com.example.front.data.remote.LoginRequest
import com.example.front.R
import android.widget.EditText
import android.content.Intent








class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtPass = findViewById<EditText>(R.id.edtPass)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnMe = findViewById<Button>(R.id.btnMe)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val txtResult = findViewById<TextView>(R.id.txtResult)
        val btnCompose = findViewById<Button>(R.id.btnCompose)
        btnCompose.setOnClickListener {
            startActivity(Intent(this, ComposeLoginActivity::class.java))
        }


        val api = ApiClient.api

        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val pass = edtPass.text.toString()

            if (email.isEmpty() || pass.isEmpty()) {
                txtResult.text = "Ingresa email y contraseña"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val res = api.login(LoginRequest(email = email, password = pass))
                    if (res.isSuccessful) {
                        Toast.makeText(this@MainActivity, "Login OK", Toast.LENGTH_SHORT).show()
                        txtResult.text = "Login OK (JSESSIONID guardada)"
                    } else {
                        txtResult.text = "Login error: ${res.code()}"
                    }
                } catch (e: Exception) {
                    txtResult.text = "Excepción: ${e.message}"
                }
            }
        }

        btnMe.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val res = api.me()
                    if (res.isSuccessful && res.body() != null) {
                        val u = res.body()!!
                        txtResult.text = "ME: ${u.email} (${u.role.name})"
                    } else {
                        txtResult.text = "ME error: ${res.code()}"
                    }
                } catch (e: Exception) {
                    txtResult.text = "Excepción: ${e.message}"
                }
            }
        }

        btnLogout.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val res = api.logout()
                    if (res.isSuccessful) {
                        txtResult.text = "Logout OK"
                    } else {
                        txtResult.text = "Logout error: ${res.code()}"
                    }
                } catch (e: Exception) {
                    txtResult.text = "Excepción: ${e.message}"
                }
            }
        }
    }
}