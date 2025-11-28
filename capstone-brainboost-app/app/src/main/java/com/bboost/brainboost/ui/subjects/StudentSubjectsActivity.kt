package com.bboost.brainboost.ui.subjects

import android.content.Intent
import androidx.compose.foundation.background

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bboost.brainboost.dto.SubjectInfoDto
import com.bboost.brainboost.network.RetrofitClient
import com.bboost.brainboost.ui.theme.BrainBoostTheme
import com.bboost.brainboost.ui.topics.TopicListActivity
import com.bboost.brainboost.util.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StudentSubjectsActivity : ComponentActivity() {

    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        session = SessionManager(this)

        setContent {
            BrainBoostTheme {
                SubjectListScreen(
                    session = session,
                    onBack = { finish() },
                    onSubjectClick = { subject ->
                        val intent = Intent(this, TopicListActivity::class.java).apply {
                            putExtra(TopicListActivity.EXTRA_SUBJECT_ID, subject.id.toString())
                            putExtra(TopicListActivity.EXTRA_SUBJECT_NAME, subject.name)
                        }
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectListScreen(
    session: SessionManager,
    onBack: () -> Unit,
    onSubjectClick: (SubjectInfoDto) -> Unit
) {
    var subjects by remember { mutableStateOf<List<SubjectInfoDto>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    val userId = session.getUserId()
    val role = session.getRole()?.lowercase()
    val token = session.getToken()

    // ---- Cargar asignaturas ----
    LaunchedEffect(Unit) {
        if (userId == null || role == null || token == null) {
            loading = false
            return@LaunchedEffect
        }

        val resp = withContext(Dispatchers.IO) {
            val bearer = "Bearer $token"
            when (role) {
                "alumno" -> RetrofitClient.instance.getStudentSubjects(bearer, userId)
                "profesor" -> RetrofitClient.instance.getProfessorSubjects(bearer, userId)
                else -> null
            }
        }

        loading = false

        resp?.let {
            if (it.isSuccessful) {
                subjects = it.body().orEmpty()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis Asignaturas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->

        // Fondo con gradiente suave
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF8F8F8),
                            Color(0xFFFFE3C2),
                            Color(0xFFD7C2FF)
                        )
                    )
                )
        ) {

            when {
                loading -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                subjects.isEmpty() -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No tienes asignaturas inscritas")
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(subjects) { subj ->
                            SubjectCard(
                                subject = subj,
                                onClick = { onSubjectClick(subj) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectCard(
    subject: SubjectInfoDto,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            Text(
                text = subject.name,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Inscrito: ${subject.enrollmentDate}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Activa: ${if (subject.active) "Sí" else "No"}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
