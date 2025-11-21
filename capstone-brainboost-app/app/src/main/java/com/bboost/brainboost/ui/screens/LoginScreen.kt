package com.bboost.brainboost.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.bboost.brainboost.R

@Composable
fun LoginScreen(
    onLogin: (
        email: String,
        password: String,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) -> Unit
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val buttonScale by animateFloatAsState(
        targetValue = if (isLoading) 0.96f else 1f,
        animationSpec = tween(150),
        label = ""
    )

    // =============== MAIN COLUMN ===============
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(90.dp))

        // ---------- LOGO ----------
        Image(
            painter = painterResource(id = R.drawable.bb),
            contentDescription = "Logo BrainBoost",
            modifier = Modifier
                .size(160.dp)
                .padding(bottom = 12.dp)
        )

        // ---------- TÍTULO ----------
        Text(
            "Iniciar sesión",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(12.dp))

        // =============== GRADIENTE DESDE AQUÍ ===============
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White,          // arriba blanco
                            Color(0xFFFFE0C2),    // naranjo pastel
                            Color(0xFFF4D1FF),    // rosado suave
                            Color(0xFFE2D8FF),    // morado suave
                        )
                    )
                )
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(28.dp))

            // ---------- INPUT EMAIL ----------
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                enabled = !isLoading
            )

            Spacer(Modifier.height(18.dp))

            // ---------- INPUT PASSWORD ----------
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                enabled = !isLoading
            )

            Spacer(Modifier.height(32.dp))

            // ---------- BOTÓN LOGIN ----------
            Button(
                onClick = {
                    isLoading = true
                    errorMessage = null

                    onLogin(
                        email,
                        password,
                        { error ->
                            errorMessage = error
                            isLoading = false
                        },
                        {
                            isLoading = false
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .scale(buttonScale),
                shape = RoundedCornerShape(14.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2A2E65)
                )
            ) {
                Text("Ingresar", style = MaterialTheme.typography.titleMedium)
            }

            if (isLoading) {
                Spacer(Modifier.height(24.dp))
                CircularProgressIndicator()
            }

            errorMessage?.let {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
