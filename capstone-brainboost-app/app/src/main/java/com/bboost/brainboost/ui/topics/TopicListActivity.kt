package com.bboost.brainboost.ui.topics

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bboost.brainboost.dto.TopicDto
import com.bboost.brainboost.network.RetrofitClient
import com.bboost.brainboost.ui.concepts.UploadConceptsActivity
import com.bboost.brainboost.ui.hangman.HangmanActivity
import com.bboost.brainboost.ui.questions.UploadQuestionsActivity
import com.bboost.brainboost.ui.quiz.QuizActivity
import com.bboost.brainboost.ui.theme.BrainBoostTheme
import com.bboost.brainboost.util.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TopicListActivity : ComponentActivity() {

    companion object {
        const val EXTRA_SUBJECT_ID = "EXTRA_SUBJECT_ID"
        const val EXTRA_SUBJECT_NAME = "EXTRA_SUBJECT_NAME"
    }

    private lateinit var session: SessionManager
    private var subjectId: String? = null
    private var subjectName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        session = SessionManager(this)
        subjectId = intent.getStringExtra(EXTRA_SUBJECT_ID)
        subjectName = intent.getStringExtra(EXTRA_SUBJECT_NAME)

        if (subjectId.isNullOrBlank()) {
            finish()
            return
        }

        setContent {
            BrainBoostTheme {
                TopicListScreen(
                    subjectName = subjectName ?: "",
                    subjectId = subjectId!!,
                    role = session.getRole()?.lowercase() ?: "alumno",
                    token = session.getToken() ?: "",
                    onBack = { finish() },
                    onHangmanClick = { startHangman(it) },
                    onQuizClick = { startQuiz(it) },
                    onGenerateClick = { startContentDialog(it) }
                )
            }
        }
    }

    private fun startHangman(topic: TopicDto) {
        val i = Intent(this, HangmanActivity::class.java).apply {
            putExtra(HangmanActivity.EXTRA_SUBJECT_ID, subjectId)
            putExtra(HangmanActivity.EXTRA_SUBJECT_NAME, subjectName)
            putExtra(HangmanActivity.EXTRA_TOPIC_ID, topic.id.toString())
            putExtra(HangmanActivity.EXTRA_TOPIC_NAME, topic.name)
        }
        startActivity(i)
    }

    private fun startQuiz(topic: TopicDto) {
        val i = Intent(this, QuizActivity::class.java).apply {
            putExtra(QuizActivity.EXTRA_SUBJECT_ID, subjectId)
            putExtra(QuizActivity.EXTRA_SUBJECT_NAME, subjectName)
            putExtra(QuizActivity.EXTRA_TOPIC_ID, topic.id.toString())
            putExtra(QuizActivity.EXTRA_TOPIC_NAME, topic.name)
        }
        startActivity(i)
    }

    // 👉 ESTE ES EL CORRECTO (solo dentro de la Activity)
    private fun startContentDialog(topic: TopicDto) {

        val mode = intent.getStringExtra("MODE") ?: "quiz"

        val destination = if (mode == "concepts")
            UploadConceptsActivity::class.java
        else
            UploadQuestionsActivity::class.java

        val i = Intent(this, destination).apply {
            putExtra("EXTRA_TOPIC_ID", topic.id.toString())
            putExtra("EXTRA_TOPIC_NAME", topic.name)
            putExtra("EXTRA_SUBJECT_ID", subjectId)
            putExtra("EXTRA_SUBJECT_NAME", subjectName)
            putExtra("MODE", mode)
        }

        startActivity(i)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicListScreen(
    subjectName: String,
    subjectId: String,
    role: String,
    token: String,
    onBack: () -> Unit,
    onHangmanClick: (TopicDto) -> Unit,
    onQuizClick: (TopicDto) -> Unit,
    onGenerateClick: (TopicDto) -> Unit
) {

    var topics by remember { mutableStateOf<List<TopicDto>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val bearer = "Bearer $token"

        val resp = withContext(Dispatchers.IO) {
            RetrofitClient.instance.getTopicsBySubject(
                bearer,
                subjectId
            )
        }

        if (resp.isSuccessful) topics = resp.body().orEmpty()
        loading = false
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Temas — $subjectName") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        if (loading) {
            Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (topics.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                Text("No hay temas disponibles")
            }
            return@Scaffold
        }

        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(topics) { topic ->
                TopicCard(
                    topic = topic,
                    role = role,
                    onHangmanClick = { onHangmanClick(topic) },
                    onQuizClick = { onQuizClick(topic) },
                    onGenerateClick = { onGenerateClick(topic) }
                )
            }
        }
    }
}


@Composable
fun TopicCard(
    topic: TopicDto,
    role: String,
    onHangmanClick: () -> Unit,
    onQuizClick: () -> Unit,
    onGenerateClick: () -> Unit
) {

    ElevatedCard(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(modifier = Modifier.padding(18.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = topic.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Icon(Icons.Default.ArrowForward, null)
            }

            Spacer(Modifier.height(10.dp))

            if (role == "alumno") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onHangmanClick,
                        modifier = Modifier.weight(1f)
                    ) { Text("Ahorcado") }

                    Button(
                        onClick = onQuizClick,
                        modifier = Modifier.weight(1f)
                    ) { Text("Quiz") }
                }
            }

            if (role == "profesor" || role == "admin") {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onGenerateClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Generar contenidos")
                }
            }
        }
    }
}
