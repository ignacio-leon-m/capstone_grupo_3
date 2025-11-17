package com.bboost.brainboost.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bboost.brainboost.dto.AiQuestionsResponse
import com.bboost.brainboost.dto.QuestionCreateDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun QuizEditScreen(
    response: AiQuestionsResponse,
    subjectId: UUID,
    topicId: UUID,
    onFinish: () -> Unit
) {
    val questions = remember { response.questions.toMutableList() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "Editar Preguntas",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(questions) { index, question ->

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text("Pregunta ${index + 1}", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))

                        // Editar texto de la pregunta
                        var editedText by remember { mutableStateOf(question.text) }
                        OutlinedTextField(
                            value = editedText,
                            onValueChange = {
                                editedText = it
                                questions[index] = questions[index].copy(text = it)
                            },
                            label = { Text("Texto de la pregunta") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(12.dp))
                        Text("Respuesta correcta:")

                        var correctAnswer by remember { mutableStateOf(question.correctAnswer) }

                        OutlinedTextField(
                            value = correctAnswer,
                            onValueChange = {
                                correctAnswer = it
                                questions[index] = questions[index].copy(correctAnswer = it)
                            },
                            label = { Text("Respuesta") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch {
                    snackbarHostState.showSnackbar("Guardando preguntas…")
                }

                val toSave = questions.map {
                    QuestionCreateDto(
                        text = it.text,
                        correctAnswer = it.correctAnswer,
                        subjectId = subjectId,
                        topicId = topicId
                    )
                }

                // Backend ya las guardó al generarlas, pero aquí estarías guardando nuevas si quieres
                onFinish()
            }
        ) {
            Text("Guardar y Volver")
        }

        Spacer(Modifier.height(16.dp))

        SnackbarHost(snackbarHostState)
    }
}
