package com.bboost.brainboost.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bboost.brainboost.dto.AiQuizDto
import com.bboost.brainboost.dto.QuestionCreateDto
import java.util.UUID

@Composable
fun QuizPreviewScreen(
    quizDto: AiQuizDto,
    selectedSubjectId: UUID?,
    selectedTopicId: UUID?,
    onSave: (List<QuestionCreateDto>) -> Unit,
    onCancel: () -> Unit
) {
    val selectedAnswers = remember { mutableStateListOf<Int?>() }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(quizDto) {
        selectedAnswers.clear()
        selectedAnswers.addAll(List(quizDto.questions.size) { null })
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Vista previa del Quiz generado", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(quizDto.questions) { index, question ->
                Text("${index + 1}. ${question.question}", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    question.options.forEachIndexed { optIndex, option ->
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedAnswers[index] == optIndex,
                                onClick = { selectedAnswers[index] = optIndex }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(option)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onCancel) {
                Text("Cancelar")
            }
            Button(
                onClick = {
                    if (selectedSubjectId != null && selectedTopicId != null) {
                        val toSave = quizDto.questions.mapIndexedNotNull { i, q ->
                            val selectedIndex = selectedAnswers[i]
                            if (selectedIndex != null) {
                                QuestionCreateDto(
                                    text = q.question,
                                    correctAnswer = q.options[selectedIndex],
                                    subjectId = selectedSubjectId,
                                    topicId = selectedTopicId
                                )
                            } else null
                        }
                        onSave(toSave)
                    }
                },
                enabled = selectedAnswers.none { it == null }
            ) {
                Text("Guardar Quiz")
            }
        }
    }
}
