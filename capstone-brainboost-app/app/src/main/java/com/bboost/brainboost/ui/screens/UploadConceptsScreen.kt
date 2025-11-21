package com.bboost.brainboost.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UploadConceptsScreen(
    topicName: String,
    subjectName: String,
    fileName: String?,
    onSelectFile: () -> Unit,
    onSubmit: (
        onResult: (String) -> Unit,
        onError: (String) -> Unit,
        onLoading: (Boolean) -> Unit
    ) -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Generar Conceptos desde PDF", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        Text("Asignatura: $subjectName")
        Text("Tema: $topicName")

        Spacer(Modifier.height(20.dp))

        // Nombre del archivo
        Text(fileName ?: "Ningún archivo seleccionado")

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = onSelectFile,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Seleccionar PDF")
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                onSubmit(
                    { result -> resultText = result },
                    { error -> errorText = error },
                    { loading -> isLoading = loading }
                )
            },
            enabled = fileName != null && !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generar e Insertar Conceptos")
        }

        Spacer(Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        }

        if (errorText.isNotBlank()) {
            Text(errorText, color = MaterialTheme.colorScheme.error)
        }

        if (resultText.isNotBlank()) {
            Text(resultText)
        }
    }
}
