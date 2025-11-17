package com.bboost.brainboost.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bboost.brainboost.R
import com.bboost.brainboost.ui.screens.components.HomeOptionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    role: String,
    onNavigateToContent: () -> Unit,
    onNavigateToUploadStudents: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToConcepts: () -> Unit,  // ← NUEVO
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.bb),
                            contentDescription = "Logo",
                            modifier = Modifier.size(40.dp).padding(end = 12.dp)
                        )
                        Text("BrainBoost", style = MaterialTheme.typography.headlineSmall)
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            Text("¡Bienvenido!", style = MaterialTheme.typography.headlineMedium)

            if (role == "profesor" || role == "admin") {

                HomeOptionCard(
                    title = "Generar Quiz con IA",
                    icon = Icons.Default.UploadFile,
                    onClick = onNavigateToContent
                )

                HomeOptionCard(
                    title = "Generar Conceptos con IA",
                    icon = Icons.Default.School,
                    onClick = onNavigateToConcepts
                )

                HomeOptionCard(
                    title = "Cargar Alumnos (Excel)",
                    icon = Icons.Default.GroupAdd,
                    onClick = onNavigateToUploadStudents
                )
            }

            if (role == "alumno") {

                HomeOptionCard(
                    title = "Mis Asignaturas",
                    icon = Icons.Default.List,
                    onClick = onNavigateToContent
                )

                HomeOptionCard(
                    title = "Jugar (Hangman / Quiz)",
                    icon = Icons.Default.SportsEsports,
                    onClick = onNavigateToContent
                )
            }
        }
    }
}
