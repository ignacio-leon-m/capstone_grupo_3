package com.bboost.brainboost.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UploadQuestionsScreen(
    topicName: String,
    subjectName: String,
    fileName: String?,      // ← nombre del PDF seleccionado
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
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TÍTULO
        Text(
            "Generar Preguntas desde PDF",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // INFORMACIÓN DE ASIGNATURA Y TEMA
        Text(
            "Asignatura: $subjectName",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            "Tema: $topicName",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // NOMBRE DEL ARCHIVO (si existe)
        Text(
            fileName ?: "Ningún archivo seleccionado",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        // BOTÓN PARA SELECCIONAR PDF
        Button(
            onClick = onSelectFile,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Seleccionar PDF")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BOTÓN PARA GENERAR PREGUNTAS
        Button(
            onClick = {
                onSubmit(
                    { result ->
                        resultText = result
                        errorText = ""
                    },
                    { error ->
                        errorText = error
                        resultText = ""
                    },
                    { loading ->
                        isLoading = loading
                    }
                )
            },
            enabled = fileName != null && !isLoading,   // ← SOLO si hay archivo
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generar Preguntas desde PDF")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // LOADING
        if (isLoading) {
            CircularProgressIndicator()
        }

        // ERROR
        if (errorText.isNotBlank()) {
            Text(
                errorText,
                color = MaterialTheme.colorScheme.error
            )
        }

        // RESULTADO
        if (resultText.isNotBlank()) {
            Text(resultText)
        }
    }
}
