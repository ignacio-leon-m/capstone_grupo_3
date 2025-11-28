package com.bboost.brainboost.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bboost.brainboost.dto.SubjectInfoDto
import com.bboost.brainboost.dto.TopicDto
import java.util.*

@Composable
fun ContentUploadScreen(
    fileName: String,
    subjects: List<SubjectInfoDto>,
    topics: List<TopicDto>,
    selectedSubjectId: UUID?,
    selectedTopicId: UUID?,
    onSelectFile: () -> Unit,
    onSelectSubject: (UUID) -> Unit,
    onSelectTopic: (UUID) -> Unit,
    onGenerate: (
        type: String,
        onResult: (String) -> Unit,
        onError: (String) -> Unit,
        onLoading: (Boolean) -> Unit
    ) -> Unit
) {
    val TAG = "ContentUploadScreen"

    // Logs básicos para verificar que el Composable se está renderizando
    Log.d(TAG, "🔄 Composable renderizado")
    Log.d(TAG, "   - Subjects: ${subjects.size}")
    Log.d(TAG, "   - Topics: ${topics.size}")
    Log.d(TAG, "   - Selected Subject: $selectedSubjectId")
    Log.d(TAG, "   - Selected Topic: $selectedTopicId")

    var isLoading by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }

    // Estados para dropdowns
    var expandedSubjects by remember { mutableStateOf(false) }
    var expandedTopics by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Carga de Contenido IA", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        // BOTÓN SELECCIONAR ARCHIVO
        Button(
            onClick = {
                Log.d(TAG, "📄 Botón PDF clickeado")
                onSelectFile()
            },
            enabled = !isLoading
        ) {
            Text("Seleccionar PDF")
        }

        Spacer(Modifier.height(8.dp))
        Text("Archivo: $fileName")

        Spacer(Modifier.height(16.dp))

        // DROPDOWN ASIGNATURAS
        Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
            Button(
                onClick = {
                    Log.d(TAG, "📚 Botón Asignaturas clickeado - ${subjects.size} disponibles")
                    expandedSubjects = true
                }
            ) {
                val subjectText = selectedSubjectId?.let { id ->
                    subjects.find { it.id == id }?.name ?: "Seleccionar Asignatura"
                } ?: "Seleccionar Asignatura"
                Text(subjectText)
            }

            DropdownMenu(
                expanded = expandedSubjects,
                onDismissRequest = { expandedSubjects = false }
            ) {
                if (subjects.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("No hay asignaturas") },
                        onClick = {}
                    )
                } else {
                    subjects.forEach { subject ->
                        DropdownMenuItem(
                            text = { Text(subject.name) },
                            onClick = {
                                Log.d(TAG, "🎯 Asignatura seleccionada: ${subject.name}")
                                expandedSubjects = false
                                onSelectSubject(subject.id)
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // DROPDOWN TEMAS (solo si hay asignatura seleccionada)
        if (selectedSubjectId != null) {
            val filteredTopics = topics.filter { it.subjectId == selectedSubjectId }
            Log.d(TAG, "📝 Mostrando temas para subject $selectedSubjectId - ${filteredTopics.size} temas")

            Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                Button(
                    onClick = {
                        Log.d(TAG, "📝 Botón Temas clickeado - ${filteredTopics.size} disponibles")
                        expandedTopics = true
                    }
                ) {
                    val topicText = selectedTopicId?.let { id ->
                        filteredTopics.find { it.id == id }?.name ?: "Seleccionar Tema"
                    } ?: "Seleccionar Tema"
                    Text(topicText)
                }

                DropdownMenu(
                    expanded = expandedTopics,
                    onDismissRequest = { expandedTopics = false }
                ) {
                    if (filteredTopics.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No hay temas") },
                            onClick = {}
                        )
                    } else {
                        filteredTopics.forEach { topic ->
                            DropdownMenuItem(
                                text = { Text(topic.name) },
                                onClick = {
                                    Log.d(TAG, "🎯 Tema seleccionado: ${topic.name}")
                                    expandedTopics = false
                                    onSelectTopic(topic.id)
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // BOTÓN GENERAR
        val isGenerateEnabled = selectedSubjectId != null && selectedTopicId != null && !isLoading
        Log.d(TAG, "🚀 Botón Generar - Habilitado: $isGenerateEnabled")

        Button(
            onClick = {
                Log.d(TAG, "🚀🚀🚀 GENERAR CLICKEADO")
                Log.d(TAG, "   - Subject: $selectedSubjectId")
                Log.d(TAG, "   - Topic: $selectedTopicId")

                onGenerate(
                    "quiz",
                    { result ->
                        Log.d(TAG, "✅ Resultado: $result")
                        resultText = result
                        errorText = ""
                    },
                    { error ->
                        Log.e(TAG, "❌ Error: $error")
                        errorText = error
                        resultText = ""
                    },
                    { loading ->
                        Log.d(TAG, "⏳ Loading: $loading")
                        isLoading = loading
                    }
                )
            },
            enabled = isGenerateEnabled
        ) {
            Text("Generar Quiz y Guardar")
        }

        Spacer(Modifier.height(16.dp))

        // ESTADOS
        if (isLoading) {
            CircularProgressIndicator()
            Spacer(Modifier.height(16.dp))
        }

        if (errorText.isNotBlank()) {
            Text(
                errorText,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (resultText.isNotBlank()) {
            Text(
                resultText,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}