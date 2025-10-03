package com.example.front

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.front.data.remote.ApiClient
import com.example.front.data.remote.LoginRequest
import kotlinx.coroutines.launch
import android.net.Uri

// Importamos las nuevas pantallas
import com.example.front.ui.MateriasScreen
import com.example.front.ui.TemasScreen
import com.example.front.ui.TemaDetailScreen // <--- IMPORTACIÓN AÑADIDA

class ComposeLoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AppNav() }
    }
}

@Composable
private fun AppNav() {
    val nav = rememberNavController()
    Surface(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = nav, startDestination = "login") {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        nav.navigate("materias") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }
            composable("materias") {
                MateriasScreen { materiaId, materiaName ->
                    val encoded = Uri.encode(materiaName)
                    nav.navigate("temas/$materiaId/$encoded")
                }
            }
            composable(
                route = "temas/{materiaId}/{materiaName}",
                arguments = listOf(
                    navArgument("materiaId") { type = NavType.StringType },
                    navArgument("materiaName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val materiaId = backStackEntry.arguments?.getString("materiaId") ?: ""
                val materiaName = backStackEntry.arguments?.getString("materiaName") ?: ""
                TemasScreen(
                    materiaId = materiaId,
                    materiaName = materiaName
                ) { temaId, temaTitulo ->
                    val enc = Uri.encode(temaTitulo)
                    nav.navigate("tema/$temaId/$enc")
                }
            }
            // La ruta ahora usa la nueva pantalla de detalle
            composable(
                route = "tema/{temaId}/{temaTitulo}",
                arguments = listOf(
                    navArgument("temaId") { type = NavType.StringType },
                    navArgument("temaTitulo") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val temaId = backStackEntry.arguments?.getString("temaId") ?: ""
                val temaTitulo = backStackEntry.arguments?.getString("temaTitulo") ?: ""
                TemaDetailScreen(
                    temaId = temaId,
                    temaTitulo = temaTitulo,
                    onBack = { nav.popBackStack() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginScreen(onLoginSuccess: () -> Unit) {
    val scope = rememberCoroutineScope()
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var errorMsg by rememberSaveable { mutableStateOf<String?>(null) }
    val isValid = username.isNotBlank() && password.length >= 6
    val api = remember { ApiClient.api }

    Scaffold { inner ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Iniciar sesión",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(20.dp))
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Usuario o correo") },
                        singleLine = true,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        )
                    )
                    if (errorMsg != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = errorMsg!!,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = {
                            errorMsg = null
                            isLoading = true
                            scope.launch {
                                try {
                                    val emailClean = username.trim()
                                    val passClean = password.trim()
                                    val res = api.login(LoginRequest(email = emailClean, password = passClean))
                                    if (res.isSuccessful) {
                                        val me = api.me()
                                        isLoading = false
                                        if (me.isSuccessful && me.body() != null) {
                                            onLoginSuccess()
                                        } else {
                                            onLoginSuccess()
                                        }
                                    } else {
                                        isLoading = false
                                        errorMsg = "Login fallido: ${res.code()}"
                                    }
                                } catch (e: Exception) {
                                    isLoading = false
                                    errorMsg = "Error de red: ${e.message}"
                                }
                            }
                        },
                        enabled = !isLoading && isValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        if (isLoading) CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(22.dp)
                        )
                        else Text("Ingresar")
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "usa tu correo/clave reales",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}