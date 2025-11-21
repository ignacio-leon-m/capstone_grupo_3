package com.bboost.brainboost.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bboost.brainboost.dto.AiQuizDto
import com.bboost.brainboost.dto.QuestionCreateDto
import java.util.*

@Composable
fun QuizSaveScreen(
    quizDto: AiQuizDto,
    selectedSubjectId: UUID,
    selectedTopicId: UUID,
    onSaveQuestions: (List<QuestionCreateDto>) -> Unit,
    onCancel: () -> Unit
) {
    var selectedAnswers by remember {
        mutableStateOf(List(quizDto.questions.size) { quizDto.questions[it].answerIndex })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Guardar Preguntas del Quiz", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            itemsIndexed(quizDto.questions) { index, question ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Pregunta ${index + 1}: ${question.question}")
                        Spacer(modifier = Modifier.height(8.dp))

                        question.options.forEachIndexed { optionIndex, optionText ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                RadioButton(
                                    selected = selectedAnswers[index] == optionIndex,
                                    onClick = {
                                        selectedAnswers = selectedAnswers.toMutableList().also {
                                            it[index] = optionIndex
                                        }
                                    }
                                )
                                Text(optionText)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = onCancel) {
                Text("Cancelar")
            }

            Button(onClick = {
                val questions = quizDto.questions.mapIndexed { index, q ->
                    QuestionCreateDto(
                        text = q.question,
                        correctAnswer = q.options[selectedAnswers[index]],
                        subjectId = selectedSubjectId,
                        topicId = selectedTopicId
                    )
                }
                onSaveQuestions(questions)
            }) {
                Text("Guardar Preguntas")
            }
        }
    }
}
