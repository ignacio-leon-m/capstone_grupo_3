package com.bboost.brainboost.ui.quiz

// ---------- IMPORTS ----------
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bboost.brainboost.dto.Question
import com.bboost.brainboost.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// ------------------------------------------------------------
//                      QUIZ SCREEN COMPLETO
// ------------------------------------------------------------

@Composable
fun QuizScreen(
    topicId: String,
    topicName: String,
    token: String,
    onFinish: () -> Unit
) {
    var questions by remember { mutableStateOf<List<Question>>(emptyList()) }
    var index by remember { mutableStateOf(0) }
    var loading by remember { mutableStateOf(true) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var showCorrection by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    var options by remember { mutableStateOf<List<String>>(emptyList()) }
    val Orange = Color(0xFFF15A29)

    // === CARGAR PREGUNTAS ===
    LaunchedEffect(Unit) {
        val resp = withContext(Dispatchers.IO) {
            RetrofitClient.instance.getQuestionsByTopic(
                token = "Bearer $token",
                topicId = topicId
            )
        }
        if (resp.isSuccessful) {
            val all = resp.body().orEmpty()
            questions = all.shuffled().take(5)
            loading = false
            if (questions.isNotEmpty())
                options = buildOptions(questions, 0)
        } else loading = false
    }

    // LOADING
    if (loading) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator(color = Orange)
        }
        return
    }

    // SIN PREGUNTAS
    if (questions.isEmpty()) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text("No hay preguntas para este tema.")
        }
        return
    }

    // QUIZ TERMINADO
    if (index >= questions.size) {
        QuizResultScreen(score, questions.size, onFinish)
        return
    }

    val current = questions[index]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        QuizHeader(
            topicName = topicName,
            index = index,
            total = questions.size
        )

        // ---------- TARJETA DE PREGUNTA ----------
        QuestionCard(questionText = current.text)

        // ---------- OPCIONES ----------
        QuizOptions(
            options = options,
            selectedOption = selectedOption,
            correctAnswer = current.correctAnswer,
            enabled = !showCorrection,
            onSelect = { chosen ->
                selectedOption = chosen
                showCorrection = true
                if (chosen == current.correctAnswer) score++
            }
        )

        Spacer(Modifier.height(10.dp))

        if (showCorrection) {
            Button(
                onClick = {
                    selectedOption = null
                    showCorrection = false
                    index++
                    if (index < questions.size)
                        options = buildOptions(questions, index)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    if (index < questions.size - 1) "Siguiente" else "Finalizar",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ------------------------------------------------------------
//                      HEADER MODERNO
// ------------------------------------------------------------

@Composable
fun QuizHeader(topicName: String, index: Int, total: Int) {

    val Orange = Color(0xFFF15A29)
    val progress = (index + 1) / total.toFloat()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        Text(
            text = topicName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .weight(1f)
                    .height(10.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(50)
                    ),
                color = Orange
            )

            Text(
                text = "${index + 1}/$total",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ------------------------------------------------------------
//                     TARJETA DE PREGUNTA
// ------------------------------------------------------------

@Composable
fun QuestionCard(questionText: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Text(
                questionText,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

// ------------------------------------------------------------
//                       OPCIONES
// ------------------------------------------------------------

@Composable
fun QuizOptions(
    options: List<String>,
    selectedOption: String?,
    correctAnswer: String,
    enabled: Boolean,
    onSelect: (String) -> Unit
) {

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        options.forEach { option ->

            val isCorrect = option == correctAnswer
            val isSelected = option == selectedOption

            val bgColor by animateColorAsState(
                targetValue = when {
                    !enabled && isSelected && isCorrect -> Color(0xFFB7FFCA)
                    !enabled && isSelected && !isCorrect -> Color(0xFFFFC3C3)
                    else -> MaterialTheme.colorScheme.surfaceVariant
                },
                label = ""
            )

            val scale by animateFloatAsState(
                targetValue = if (isSelected) 0.97f else 1f,
                label = ""
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(scale),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = bgColor),
                elevation = CardDefaults.cardElevation(4.dp),
                onClick = { if (enabled) onSelect(option) }
            ) {
                Text(
                    text = option,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

// ------------------------------------------------------------
//                   PANTALLA DE RESULTADO
// ------------------------------------------------------------

@Composable
fun QuizResultScreen(score: Int, total: Int, onFinish: () -> Unit) {

    val Orange = Color(0xFFF15A29)

    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            val message =
                if (score == total) "🎉 ¡Perfecto!" else "🔥 Quiz completado"

            Text(message, style = MaterialTheme.typography.headlineLarge)

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Puntaje: $score / $total",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onFinish,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(55.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange,
                    contentColor = Color.White
                )
            ) {
                Text("Volver a Temas", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ------------------------------------------------------------
//              GENERADOR DE ALTERNATIVAS
// ------------------------------------------------------------

fun buildOptions(all: List<Question>, index: Int): List<String> {
    val correct = all[index].correctAnswer
    val distractors = all
        .map { it.correctAnswer }
        .filter { it != correct }
        .shuffled()
        .take(3)

    return (distractors + correct).shuffled()
}
