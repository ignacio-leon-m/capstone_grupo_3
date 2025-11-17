package com.bboost.brainboost.ui.screens

// ---------- IMPORTS ----------
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bboost.brainboost.dto.*
import com.bboost.brainboost.network.RetrofitClient
import kotlinx.coroutines.launch


// ------------------------------------------------------------
//                      HANGMAN SCREEN
// ------------------------------------------------------------

@Composable
fun HangmanScreen(
    subjectName: String,
    token: String,
    gameState: HangmanGameStateDto,
    onExit: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var currentIndex by remember { mutableStateOf(gameState.currentConceptIndex) }
    var concept by remember { mutableStateOf(gameState.concepts[currentIndex]) }
    var maskedWord by remember { mutableStateOf(maskWord(concept.word)) }

    var usedLetters by remember { mutableStateOf(mutableSetOf<Char>()) }
    var isProcessing by remember { mutableStateOf(false) }

    var lives by remember { mutableStateOf(gameState.livesRemaining) }
    var conceptSolved by remember { mutableStateOf(false) }
    var conceptStartTime by remember { mutableStateOf(System.currentTimeMillis()) }

    val totalConcepts = gameState.concepts.size

    // ---------- SCROLL GENERAL ----------
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Brush.verticalGradient(listOf(Color(0xFFFFEAD7), Color.White)))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ---------- TÍTULO ----------
        Text(
            text = "Ahorcado — $subjectName",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2F2F2F)
        )

        // ---------- VIDAS ----------
        Row {
            repeat(lives) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Red)
            }
        }

        // ---------- PROGRESO ----------
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            repeat(totalConcepts) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(if (it <= currentIndex) Color(0xFFF15A29) else Color.LightGray)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
        }

        // ---------- CANVAS MODERNO ----------
        HangmanCanvas(lives = lives)

        // ---------- PISTA ----------
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(500))
        ) {
            Text(
                text = "Pista: ${concept.hint ?: "Sin pista"}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333)
            )
        }

        // ------------------------------------------------------------
        //                  PALABRA ANIMADA
        // ------------------------------------------------------------
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            maskedWord.forEachIndexed { i, char ->

                val scale by animateFloatAsState(
                    targetValue = if (char != '_') 1.1f else 1f,
                    animationSpec = tween(300),
                    label = ""
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7EF)),
                    modifier = Modifier
                        .padding(4.dp)
                        .width(42.dp)
                        .height(50.dp)
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (char == '_') "" else char.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2F2F2F)
                        )
                    }
                }
            }
        }

        // ------------------------------------------------------------
        //                      TECLADO INTELIGENTE
        // ------------------------------------------------------------
        HangmanKeyboard(
            enabled = !conceptSolved && !isProcessing,
            usedLetters = usedLetters,
            onLetterPress = { letter ->
                if (letter[0] in usedLetters) return@HangmanKeyboard
                usedLetters.add(letter[0])
                isProcessing = true

                scope.launch {
                    val resp = RetrofitClient.instance.attemptLetter(
                        "Bearer $token",
                        gameState.gameId.toString(),
                        HangmanAttemptDto(
                            conceptId = concept.id,
                            letter = letter.lowercase(),
                            responseTimeMs = 0L
                        )
                    )

                    isProcessing = false

                    if (!resp.isSuccessful || resp.body() == null) {
                        Toast.makeText(context, "Error al procesar la letra", Toast.LENGTH_LONG).show()
                        return@launch
                    }

                    val body = resp.body()!!
                    lives = body.livesRemaining

                    // ✔ Correcta
                    if (body.isCorrect) {
                        body.positions.forEach { idx ->
                            maskedWord[idx] = concept.word[idx]
                        }

                        if (maskedWord.none { it == '_' }) {
                            conceptSolved = true
                            RetrofitClient.instance.submitConcept(
                                "Bearer $token",
                                gameState.gameId.toString(),
                                HangmanConceptSubmitDto(
                                    concept.id,
                                    true,
                                    System.currentTimeMillis() - conceptStartTime
                                )
                            )
                        }

                    } else if (body.gameOver) {
                        val end = RetrofitClient.instance.endHangman(
                            "Bearer $token",
                            gameState.gameId.toString()
                        ).body()

                        Toast.makeText(context, "Juego terminado. Puntaje: ${end?.finalScore}", Toast.LENGTH_LONG).show()
                        onExit()
                    }
                }
            }
        )

        // ---------- BOTÓN SIGUIENTE ----------
        AnimatedVisibility(
            visible = conceptSolved,
            enter = slideInVertically(tween(300)) + fadeIn(),
            exit = fadeOut()
        ) {
            Button(
                onClick = {
                    if (currentIndex + 1 < totalConcepts) {
                        currentIndex++
                        concept = gameState.concepts[currentIndex]
                        maskedWord = maskWord(concept.word)
                        usedLetters.clear()
                        conceptSolved = false
                        conceptStartTime = System.currentTimeMillis()
                    } else {
                        scope.launch {
                            val end = RetrofitClient.instance.endHangman(
                                "Bearer $token",
                                gameState.gameId.toString()
                            ).body()

                            Toast.makeText(context, "Puntaje final: ${end?.finalScore}", Toast.LENGTH_LONG).show()
                            onExit()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Siguiente")
            }
        }
    }
}


// ------------------------------------------------------------
//                      CANVAS DEL MONO
// ------------------------------------------------------------

@Composable
fun HangmanCanvas(lives: Int) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        val w = size.width
        val h = size.height
        val baseY = h * 0.9f

        drawLine(Color.Black, Offset(w * 0.1f, baseY), Offset(w * 0.9f, baseY), 10f)
        drawLine(Color.DarkGray, Offset(w * 0.2f, baseY), Offset(w * 0.2f, h * 0.1f), 7f)
        drawLine(Color.DarkGray, Offset(w * 0.2f, h * 0.1f), Offset(w * 0.55f, h * 0.1f), 7f)

        val showHead = lives <= 4
        val showBody = lives <= 3
        val showArms = lives <= 2
        val showLegs = lives <= 1

        if (showHead) drawCircle(Color.Black, 20f, Offset(w * 0.55f, h * 0.26f))
        if (showBody) drawLine(Color.Black, Offset(w * 0.55f, h * 0.30f), Offset(w * 0.55f, h * 0.52f), 7f)
        if (showArms) {
            drawLine(Color.Black, Offset(w * 0.55f, h * 0.38f), Offset(w * 0.49f, h * 0.45f), 7f)
            drawLine(Color.Black, Offset(w * 0.55f, h * 0.38f), Offset(w * 0.61f, h * 0.45f), 7f)
        }
        if (showLegs) {
            drawLine(Color.Black, Offset(w * 0.55f, h * 0.52f), Offset(w * 0.50f, h * 0.68f), 7f)
            drawLine(Color.Black, Offset(w * 0.55f, h * 0.52f), Offset(w * 0.60f, h * 0.68f), 7f)
        }
    }
}


// ------------------------------------------------------------
//              TECLADO MODERNO (con feedback visual)
// ------------------------------------------------------------

@Composable
fun HangmanKeyboard(
    enabled: Boolean,
    usedLetters: Set<Char>,
    onLetterPress: (String) -> Unit
) {
    val rows = listOf(
        "QWERTYUIOP".toList(),
        "ASDFGHJKL".toList(),
        "ZXCVBNM".toList()
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                row.forEach { letter ->

                    val isUsed = letter in usedLetters

                    ElevatedCard(
                        shape = CircleShape,
                        colors = CardDefaults.elevatedCardColors(
                            containerColor =
                                when {
                                    !isUsed -> Color(0xFFF15A29)         // normal
                                    else -> Color(0xFFB0B0B0)            // usado
                                }
                        ),
                        modifier = Modifier
                            .padding(3.dp)
                            .size(40.dp)
                            .clickable(enabled = enabled && !isUsed) {
                                onLetterPress(letter.toString())
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = letter.toString(),
                                color = if (isUsed) Color.DarkGray else Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}


// ---------- MASK ----------
private fun maskWord(word: String): CharArray =
    word.map { if (it.isLetter()) '_' else it }.toCharArray()
